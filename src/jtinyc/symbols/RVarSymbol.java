package jtinyc.symbols;

/** 反射得到的变量符号(包括字段、方法参数) */
public class RVarSymbol extends BVarSymbol {
    RVarSymbol(String name, VarSymbolKind kind, BTypeSymbol varType) {
        super(name, kind);
        this.varType = varType;
    }
}
