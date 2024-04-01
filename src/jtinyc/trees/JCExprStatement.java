package jtinyc.trees;

/** 表达式语句 */
public class JCExprStatement extends JCStatement
{
    /** 语句的表达式*/
    public JCExpression expr;

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitExprStmt(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D arg) {
        return v.translateExpressionStatement(this, arg);
    }
}
