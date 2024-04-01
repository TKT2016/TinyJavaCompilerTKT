/*
 * Jtiny compiler source code
 * License : the GNU General Public License
 * Copyright (C) 2021-2024 Chines JSSQSY WANG
 */
package jtinyc.trees;

import jtinyc.symbols.DSourceFileSymbol;
import jtinyc.symbols.SymbolScope;

import java.util.ArrayList;
/** 源文件语法树 */
public class JCFile extends JCTree
{
    /** 文件顶部定义的包 */
    public JCPackage packageDecl;

    /** 文件导入类型 */
    public ArrayList<JCImport> imports;

    /** 文件内定义的函数 */
    public ArrayList<JCMethod> methods;

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) {
        v.visitFile(this,  arg);
    }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D arg) {
        return v.translateFile(this, arg);
    }

    /** 符号 */
    public DSourceFileSymbol fileSymbol;

    /** 作用域 */
    public SymbolScope symbolScope;
}
