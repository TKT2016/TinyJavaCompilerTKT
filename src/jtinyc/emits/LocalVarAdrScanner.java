package jtinyc.emits;

import jtinyc.symbols.DVarSymbol;
import jtinyc.symbols.DMethodSymbol;
import jtinyc.trees.*;
import jtinyc.trees.TreeScanner;

/** 变量地址计算上下文参数 */
class LocalVarAdrContext
{
    public int adr;//局部变量地址

    public LocalVarAdrContext(int adr)
    {
        this.adr = adr;
    }
}

/** 局部变量地址计算扫描器 */
public class LocalVarAdrScanner extends TreeScanner<LocalVarAdrContext>
{
    public void visit(JCMethod tree)
    {
        visitMethodDef(tree,null);
    }

    /** 扫描函数 */
    @Override
    public void visitMethodDef(JCMethod tree, LocalVarAdrContext arg)
    {
        DMethodSymbol declMethodSymbol = tree.methodSymbol;
        /* 计算方法参数的地址 */
        /** 非静态函数要传递this,所以方法的参数要从1开始 */
        int startAdr = declMethodSymbol.isStatic?0:1;

        LocalVarAdrContext context = new LocalVarAdrContext(startAdr);
        /* 按参数顺序依次给参数地址赋值 */
        for(int i=0;i<declMethodSymbol.getParameterCount();i++)
        {
            DVarSymbol symbol = declMethodSymbol.getParameterSymbol(i);
            setAdr(symbol,context);
        }

        /* 扫描函数体变量地址 */
        tree.body.scan(this,context );
    }

    /** 扫描变量声明表达式 */
    @Override
    public void visitVarDef(JCVariableDecl tree, LocalVarAdrContext arg) {
        setAdr((DVarSymbol)tree.nameExpr.symbol,arg);
    }

    /** 设置变量符号的地址 */
    private void setAdr(DVarSymbol symbol, LocalVarAdrContext arg)
    {
        symbol.adr =arg.adr; //设置参数地址
        arg.adr++; //地址自增1
    }

    /** 扫描代码块 */
    @Override
    public void visitBlock(JCBlock tree, LocalVarAdrContext arg) {
        /* 一个代码块就对应一个作用域，需要创建一个新的 LocalVarAdrContext */
        LocalVarAdrContext newContext = new LocalVarAdrContext(arg.adr);
        for(JCTree stmt:tree.statements)
        {
            if(stmt instanceof JCExprStatement
                    || stmt instanceof JCIf
                  //  || stmt instanceof JCForLoop
                    ||stmt instanceof JCWhile
                    ||stmt instanceof JCBlock )
                stmt.scan(this,newContext);//用新的LocalVarAdrContext进行计算
        }
    }

    /** 扫描for循环 */
   // @Override
   // public void visitForLoop(JCForLoop tree, LocalVarAdrContext arg)
 //   {
        /* for循环初始化语句声明的变量作用域只在这个循环内有效，所以要创建一个新的LocalVarAdrContext */
   //     LocalVarAdrContext newContext = new LocalVarAdrContext(arg.adr);
    //    if(tree.init!=null) //for循环可能声明变量,所以要扫描
    //        tree.init.scan(this,newContext);
    //    tree.body.scan(this,newContext);
    //}

    /** 扫描表达式语句  */
    @Override
    public void visitExprStmt(JCExprStatement tree, LocalVarAdrContext arg)
    {
        if(tree.expr instanceof JCVariableDecl)
            tree.expr.scan(this,arg);//只扫描变量声明表达式
    }
}
