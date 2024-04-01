package jtinyc.utils;

import jtinyc.lex.Token;
import jtinyc.trees.JCTree;
import tools.StringHelper;

/* 源文件Log */
public class SourceLog extends SimpleLog {
    //public static final int NOPOS  = -1;
    public final String sourceFile;
    final String sourceCode;

    public SourceLog(CompileContext context, String sourceFile, String code)
    {
        super(context);
        this.sourceFile = sourceFile;
        this.sourceCode= code;
    }

    public void error( String msg , JCTree tree)
    {
        context.errors++;
        print(tree.pos,tree.line,msg,"错误",tree.toString().length());
    }

    public void error(int pos,int line, String msg ,int keywordSize)
    {
        context.errors++;
        print(pos,line,msg,"错误",keywordSize);
    }

    public void error(Token token ,String msg )
    {
        context.errors++;
        print(token.pos,token.line,msg,"错误",token.getSize());
    }

    public void print(int pos,int line, String msg,String head,int keywordSize)
    {
        int pos1 = StringHelper.getNewLineCharPosForward (sourceCode,pos)+1;
        int pos2 = StringHelper.getNewLineCharPosBackward(sourceCode,pos);
        if(pos1>=pos2)
            pos1=pos2-1;
        String lineCode = sourceCode.substring(pos1,pos2);
        int col = pos-pos1+1;

        StringBuilder buff = new StringBuilder();
        buff.append(head+" ");
        buff.append(sourceFile);
        buff.append(" ");
        buff.append(line+"行 " );
        buff.append(col+"列 : ");
        buff.append(msg);

        buff.append("\n");
        for(int i=0;i<lineCode.length();i++)
        {
            char ch = lineCode.charAt(i);
            if(col==i+1) buff.append("\033[7m"); // buff.append("\033[41;1m");
            buff.append(ch);
            if(col  ==i -keywordSize+2) buff.append("\033[0m");
        }

        buff.append("\n");
        response(buff.toString());
    }

}
