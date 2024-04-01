/*
 * Jtiny compiler source code
 * License : the GNU General Public License
 * Copyright (C) 2021-2024 Chines JSSQSY WANG
 */
package jtinyc.trees;
import jtinyc.symbols.SymbolScope;
import java.util.ArrayList;

/** 语句块 */
public class JCBlock extends JCStatement
{
    /** 语句块内的语句 */
    public ArrayList<JCStatement> statements;

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitBlock(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D arg) {
        return v.translateBlock(this, arg);
    }
    /** 是否是for循环的循环体 */
    //public boolean isForLoopBody;
    /** 它的作用域 */
    public SymbolScope scope;
}