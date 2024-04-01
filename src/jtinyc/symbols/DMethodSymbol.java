package jtinyc.symbols;
import tools.ListMap;

/** 源码函数符号 */
public class DMethodSymbol extends BMethodSymbol
{
    public DMethodSymbol(BTypeSymbol owner, String name, BTypeSymbol returnType )
    {
        super(name,owner,true,false,false);
        this.returnType =returnType;
    }

    /** 方法参数表 */
    public final ListMap<DVarSymbol> parameters = new ListMap<>();

    /** 添加参数 */
    public void addParameter(DVarSymbol varSymbol)
    {
        parameters.put(varSymbol.name, varSymbol);
    }

    public int getParameterCount()
    {
        return parameters.size();
    }

    @Override
    public DVarSymbol getParameterSymbol(int i)
    {
        return parameters.get(i);
    }
}
