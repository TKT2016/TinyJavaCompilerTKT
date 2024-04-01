package jtinyc.trees;

import java.util.ArrayList;

/** 方法调用表达式 */
public class JCMethodInvocation extends JCExpression
{
    /** 函数名称表达式 */
    public JCExpression methodExpr;

    /** 函数实参列表 */
    public ArrayList<JCExpression> args;

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitMethodInvocation(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D arg) {
        return v.translateMethodInvocation(this, arg);
    }

}
