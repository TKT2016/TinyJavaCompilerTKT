/*
 * Jtiny compiler source code
 * License : the GNU General Public License
 * Copyright (C) 2021-2024 Chines JSSQSY WANG
 */
package jtinyc.trees;

/** 声明变量表达式 */
public class JCVariableDecl extends JCExpression
{
    /** 变量类型 */
    public  JCExpression varType;

    /** 变量名称 */
    public  JCIdent nameExpr;

    /** 变量初始值 */
    public JCExpression init;

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitVarDef(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D arg) {
        return v.translateVariable(this, arg);
    }

}
