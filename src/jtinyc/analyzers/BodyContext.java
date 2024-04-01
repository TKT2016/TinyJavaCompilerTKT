/*
 * Jtiny compiler source code
 * License : the GNU General Public License
 * Copyright (C) 2021-2024 Chines JSSQSY WANG
 */
package jtinyc.analyzers;
import jtinyc.symbols.DMethodSymbol;
import jtinyc.symbols.SymbolScope;

/** 函数体语义分析上下文参数 */
class BodyContext {
    public DMethodSymbol methodSymbol; //当前函数符号
    public SymbolScope scope; //当前作用域

    private BodyContext copy( )
    {
        BodyContext newContext = new BodyContext();
        newContext.methodSymbol = this.methodSymbol;
        newContext.scope = this.scope;
        return newContext;
    }

     /*  克隆一个BodyContext，并创建新一层作用域 */
    public BodyContext newScope( )
    {
        BodyContext context = this.copy();
        context.scope = this.scope.createChild();
        return context;
    }
}
