/*
 * Jtiny compiler source code
 * License : the GNU General Public License
 * Copyright (C) 2021-2024 Chines JSSQSY WANG
 */
package jtinyc.trees;

import jtinyc.lex.TokenKind;

/* 基本类型表达式 */
public class JCPrimitiveType extends JCExpression
{
    /** 基本标记类型 */
    public TokenKind kind;

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) { v.visitPrimitiveType(this, arg); }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D arg) {
        return v.translatePrimitiveType(this, arg);
    }

}