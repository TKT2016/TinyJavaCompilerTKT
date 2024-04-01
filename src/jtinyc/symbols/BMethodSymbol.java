package jtinyc.symbols;
import java.util.ArrayList;

/** 函数符号基类 */
public abstract class BMethodSymbol extends Symbol
{
    public BTypeSymbol ownerType; //函数所属类型
    public final boolean isPublic;//是否是public
    public final boolean isStatic;// 是否是静态的
    public final boolean isConstructor; // 是否是构造函数
    public BTypeSymbol returnType; // 返回结果类型

    protected BMethodSymbol(String name, BTypeSymbol owner, boolean isPublic, boolean isStatic, boolean isConstructor)
    {
        super(name);
        ownerType = owner;
        this.isPublic = isPublic;
        this.isStatic = isStatic;
        this.isConstructor = isConstructor;
    }

    /** 获取参数个数 */
    public abstract int getParameterCount();

    /** 获取第i个参数符号 */
    public abstract BVarSymbol getParameterSymbol(int i);

    /* 比较参数的匹配度 */
   /* public int matchArgTypes(ArrayList<BTypeSymbol> argTypes)
    {
        // 1: 比较参数个数是否相同
        if(argTypes.size()!=this.getParameterCount())
           return -1;
        //2 : 如果参数个数都为0，则都是匹配的
        if(this.getParameterCount()==0)
            return 0;
        else {
            // 比较每个参数的匹配度，并累加；但是只要有一个参数不匹配，则这些函数都是不匹配的，直接返回-1
            int sum = 0;
            for (int i = 0; i < getParameterCount(); i++) {
                BTypeSymbol argtypeSymbol = argTypes.get(i);
                BTypeSymbol paramSymbol = getParameterSymbol(i).getTypeSymbol();
                int k = SymbolUtil.matchAssignableSymbol(paramSymbol, argtypeSymbol);
                if (k < 0)
                    return -1;
                else
                    sum += k;
            }
            return sum;
        }
    }*/
}
