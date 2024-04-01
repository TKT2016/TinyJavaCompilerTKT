package jtinyc.trees;

import jtinyc.lex.Token;

/** 访问字段表达式 */
public class JCFieldAccess extends JCExpression
{
    /** 点运算被限定的部分 */
    public JCExpression selected;
    /** 点运算名称 */
    public Token nameToken;

    @Override
    public <D> void scan(TreeScanner<D> v, D arg){ v.visitFieldAccess(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D arg) {
        return v.translateFieldAccess(this, arg);
    }
}
