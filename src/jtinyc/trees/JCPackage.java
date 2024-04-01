/*
 * Jtiny compiler source code
 * License : the GNU General Public License
 * Copyright (C) 2021-2024 Chines JSSQSY WANG
 */
package jtinyc.trees;

/** 定义包语法树  */
public class JCPackage extends JCTree
{
    /* 包名称 */
    public JCFullname packageName;

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) {
        v.visitPackage(this, arg);
    }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D arg) {
        return v.translatePackage(this, arg);
    }
}
