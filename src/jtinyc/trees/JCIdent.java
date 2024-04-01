package jtinyc.trees;

import jtinyc.lex.Token;

/** 标识符表达式 */
public class JCIdent extends JCExpression
{
    /** 标识符名称标记 */
    public Token nameToken;

    public String getName()
    {
        return nameToken.identName;
    }

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitIdent(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D arg) {
        return v.translateIdent(this, arg);
    }

}
