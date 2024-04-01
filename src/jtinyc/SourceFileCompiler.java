/*
 * Jtiny compiler source code
 * License : the GNU General Public License
 * Copyright (C) 2021-2024 Chines JSSQSY WANG
 */
package jtinyc;

import jtinyc.analyzers.*;
import jtinyc.emits.FileEmit;
import jtinyc.lex.Tokenizer;
import jtinyc.optimizers.Optimizer;
import jtinyc.parse.Parser;
import jtinyc.utils.SourceLog;
import tools.FileUtil;
import jtinyc.trees.JCFile;
import jtinyc.utils.CompileContext;
import java.io.File;
import java.io.IOException;

public class SourceFileCompiler {
    CompileContext context;

    public SourceFileCompiler( )
    {
        this.context = new CompileContext();
    }

    public CompileContext compile(String inputFile) {
        File file = new File(inputFile);
         if(! file.exists())
         {
             context.log.error(String.format("源文件不存在:%s",inputFile));
             return context;
         }

        JCFile compilationFile = parseFile(inputFile);
        if (context.errors > 0) return context;
        analyze(compilationFile);
        if (context.errors > 0) return context;
        compilationFile = optimize(compilationFile);
        new ReturnAnalyzer().visit( compilationFile);
        if (context.errors == 0) {
            new FileEmit( ).emit(compilationFile);
        }
        context.fileSymbols.add(compilationFile.fileSymbol);
        return context;
    }

    public JCFile parseFile(String fileObject)
    {
        JCFile compilationFile = parse(fileObject);
        return compilationFile;
    }

    void analyze(JCFile tree)
    {
        (new FileAnalyzer()).visitFile(tree);
        if (context.errors > 0)
            return;
        ( new BodyAnalyzer()).visitMethods(tree);
    }

    JCFile optimize(JCFile tree)
    {
        Optimizer optimizer = new Optimizer();
        JCFile tree1 = optimizer.translate(tree);
        return tree1;
    }

    JCFile parse(String file)
    {
        try {
            String code = FileUtil.readText(file);
            SourceLog log = new SourceLog( context, file,code);
            Tokenizer tokenizer = new Tokenizer(log, code);
            Parser parser = new Parser(tokenizer,log);
            JCFile tree = parser.parseFile();
            return tree;
        }
        catch (IOException e) {
            context.log.error("文件读取发生异常:" + file+":"+e.getMessage());
        }
        return null;
    }

   public void showResult()
    {
        StringBuilder builder = new StringBuilder();
        if (context.errors==0)
            builder.append("0 错误");
        else
            builder.append("有"+context.errors +"个错误");
       // builder.append(",");
       /* if (context.warnings==0)
            builder.append("0 警告");
        else
            builder.append("有"+context.errors +"个警告");*/
        context.log.response(builder.toString());
    }
}
