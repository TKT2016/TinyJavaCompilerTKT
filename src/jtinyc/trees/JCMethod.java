/*
 * Jtiny compiler source code
 * License : the GNU General Public License
 * Copyright (C) 2021-2024 Chines JSSQSY WANG
 */
package jtinyc.trees;

import jtinyc.symbols.SymbolScope;
import jtinyc.lex.Token;
import jtinyc.symbols.DMethodSymbol;
import java.util.ArrayList;

/** 定义方法树 */
public class JCMethod extends JCTree
{
    /** 函数返回值表达式 */
    public JCExpression retTypeExpr;
    /** 函数名称 */
    public Token nameToken;
    /** 函数参数列表 */
    public ArrayList<JCVariableDecl> params;
    /** 函数体语句块 */
    public JCBlock body;

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitMethodDef(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D arg) {
        return v.translateMethod(this, arg);
    }
    /** 方法符号*/
    public DMethodSymbol methodSymbol;
    /** 作用域 */
    public SymbolScope scope;
}
