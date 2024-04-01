/*
 * Jtiny compiler source code
 * License : the GNU General Public License
 * Copyright (C) 2021-2024 Chines JSSQSY WANG
 */
package jtinyc.trees;

import jtinyc.lex.Token;

import java.util.ArrayList;

/* 类或者包全名称 */
public class JCFullname extends JCExpression
{
    /** 点运算名称 */
    public ArrayList<Token> nameTokens = new ArrayList<>();

    public void add(Token name)
    {
        if(name!=null)
            nameTokens.add(name);
    }

    public Token getPosToken()
    {
        return nameTokens.get(0);
    }

    public String getFullName()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(nameTokens.get(0).identName);

        for(int i=1;i<nameTokens.size();i++ )
        {
            builder.append(".");
            builder.append(nameTokens.get(i).identName);
        }
        return builder.toString();
    }

    @Override
    public <D> void scan(TreeScanner<D> v, D arg){
        //v.visitFieldAccess(this, arg);
    }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D arg) {
        return this;// return v.translateFieldAccess(this, arg);
    }
}
