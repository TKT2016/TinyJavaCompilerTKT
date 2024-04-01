package jtinyc.trees;

import java.io.StringWriter;
import jtinyc.lex.Token;
import jtinyc.utils.SourceLog;

/** 抽象语法树父类 */
public abstract class JCTree implements Cloneable
{
    /** 在源文件中所在行号 */
    public int line;
    /** 在源文件中开始位置 */
    public int pos=-1;
    /** 报错器 */
    public SourceLog log;
    /** 用当前语法树的位置和行号以及代码加提示参数进行报错 */
    public void error(String msg)
    {
        log.error(pos,line,msg,this.toString().length());
    }
    /** 用当前语法树的位置和行号以及代码加格式化参数参数进行报错 */
    public void error(String formatter,String key)
    {
        log.error(pos,line,String.format(formatter,key),key.length());
    }
    /** 用某词法标记的信息以及提示进行报错 */
    public void error(Token token, String formatter,String key)
    {
        log.error(token,String.format(formatter,key));
    }

    /** 用 TreePretty类生成格式化代码字符串 */
    @Override
    public String toString() {
        StringWriter s = new StringWriter();
        new TreePretty(s).printTree(this);
        return s.toString();
    }

    /** TreeScanner分析  */
    public abstract <D> void scan(TreeScanner<D> v, D arg);
    /* TreeTranslator转换当前语法树得到一个新的树 */
    public abstract <D> JCTree translate(TreeTranslator<D> v, D arg);
}
