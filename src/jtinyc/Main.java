package jtinyc;

import jtinyc.lex.Tokenizer;
import jtinyc.symbols.DSourceFileSymbol;
import jtinyc.utils.SourceLog;
import jtinyc.lex.Token;
import jtinyc.lex.TokenKind;
import jtinyc.utils.CompileContext;
import tools.FileUtil;
import tools.runs.ClassRunArg;
import tools.runs.ClassRuner;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        if(args.length==0)
        {
            System.out.println("请输入一个源文件");
            return;
        }
        compileRun(args[0],true);
    }

    public static void compileRun(String inputFile,boolean checkBytes)
    {
        SourceFileCompiler fileCompiler = new SourceFileCompiler();
        CompileContext context = fileCompiler.compile(inputFile);
        fileCompiler.showResult();
        if(context==null ||fileCompiler.context.errors>0)
            return;
        DSourceFileSymbol sourceFileSymbol = context.fileSymbols.get(0);
        ClassRuner classRuner = new ClassRuner();
        String[] classPaths = new String[]{FileUtil.getCurrentPath()+"\\out"};
        try {
            byte[] bytes =  FileUtil.readAllBytes(sourceFileSymbol.compiledClassFile);
            ClassRunArg runArg = new ClassRunArg();
            runArg.classPaths = classPaths;
            runArg.checkBytes = checkBytes;
            runArg.bytes = bytes;
            runArg.className =  sourceFileSymbol.getFullname();
            runArg.method =  "main";
           // runArg.argTypes = new Class<?>[]{(new String[]{}).getClass()};
            runArg.argTypes = new Class<?>[]{};
            //runArg.args = new Object[]{new String[]{} };
            runArg.args = new Object[]{};
            Object result=  classRuner.runBytes(runArg);
        }
        catch (IOException e)
        {
            System.err.println("文件编译失败:"+e.getMessage());
        }
        catch (Exception e)
        {
            System.err.println("调用异常:"+e.getMessage());
            e.printStackTrace();
        }
    }

    /* 词法分析 */
    public static void scan(String file)
    {
        CompileContext context= new CompileContext();
        try {
            String code = FileUtil.readText(file);
            SourceLog log = new SourceLog(context, file,code);
            Tokenizer tokenizer = new Tokenizer(log, code);
            while (true)
            {
                Token token = tokenizer.readToken();
                if(token!=null&& token.kind !=TokenKind.EOF)
                {
                    System.out.println(token);
                }
                else
                    break;
            }
        }
        catch (IOException e) {
            context.log.error("文件读取发生异常:" + file+":"+e.getMessage());
        }
    }
}
