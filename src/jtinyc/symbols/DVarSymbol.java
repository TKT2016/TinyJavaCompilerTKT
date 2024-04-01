package jtinyc.symbols;

/** 源码变量符号(包括局部变量、方法参数) */
public class DVarSymbol extends BVarSymbol
{
    public DVarSymbol( String name,VarSymbolKind kind , BTypeSymbol varType ) {
        super(name,kind);
        this.varType = varType;
        this.isPublic = true; //默认public
        this.isStatic = true; //默认static
        this.writable = true; //默认可写
    }
    /* 用于字节码生成阶段 */
    public int adr = -1; //变量地址

    /* 变量所在作用域 */
    public SymbolScope scope;
}
