package jtinyc.symbols;
import java.util.HashMap;
/** 反射类型符号管理器*/
public abstract class RClassSymbolManager {
    /** void类型符号 */
    public static final RClassSymbol voidPrimitiveSymbol;
    /** boolean基本类型符号 */
    public static final RClassSymbol booleanPrimitiveSymbol;
    /** int 基本类型符号 */
    public static final RClassSymbol intPrimitiveSymbol;
    /** String 基本类型符号 */
    public static final RClassSymbol StringSymbol;
    /** Object 基本类型符号 */
    public static final RClassSymbol ObjectSymbol;

    /** 初始化时创建常用类型并保存 */
    static {
        cache = new HashMap<>();

        voidPrimitiveSymbol = new RClassSymbol(void.class);
        booleanPrimitiveSymbol = new RClassSymbol(boolean.class);
        intPrimitiveSymbol = new RClassSymbol(int.class);
        StringSymbol  = new RClassSymbol(String.class);
        ObjectSymbol  = new RClassSymbol(Object.class);

        putSymbol(voidPrimitiveSymbol);
        putSymbol(booleanPrimitiveSymbol);
        putSymbol(intPrimitiveSymbol);
        putSymbol(StringSymbol);
        putSymbol(ObjectSymbol);
    }
    /** 缓存 */
    private static HashMap<Class<?>, RClassSymbol> cache;
    /** 使用反射从类名称获取Class符号 */
    public static RClassSymbol forName(String classFullName)
    {
        try {
            Class<?> clazz = Class.forName(classFullName);
            RClassSymbol rClassSymbol = getSymbol(clazz);
            return rClassSymbol;
        }catch (ClassNotFoundException e)
        {
        }
        return null;
    }
    /** 从Class获取Class符号 */
    public static RClassSymbol getSymbol(Class<?> clazz)
    {
        if(cache.containsKey(clazz))
            return cache.get(clazz);
        RClassSymbol rClassSymbol = new RClassSymbol(clazz);
        cache.put(clazz, rClassSymbol);
        return rClassSymbol;
    }

    private static void putSymbol(RClassSymbol sym)
    {
        cache.put(sym.clazz,sym);
    }
}
