package jtinyc.symbols;
import java.lang.reflect.*;
import java.util.ArrayList;
/** 反射得到的类型符号 */
public class RClassSymbol extends BTypeSymbol {
    public final Class<?> clazz;
     RClassSymbol(Class<?> clazz)
    {
        super(clazz.getSimpleName(),  clazz.isInterface(),Modifier.isPublic(clazz.getModifiers()));
        this.clazz=clazz;
    }

    /** 查找字段 */
    @Override
    public  BVarSymbol findField(String name)
    {
        try {
            Field field = clazz.getField(name);
            if(field==null) return null;
            BVarSymbol varSymbol = RVarSymbolManager.getSymbol(field,this);
            return varSymbol;
        }catch (NoSuchFieldException|SecurityException e)
        {
            return null;
        }
    }

    /** 查找字段和方法 */
    @Override
    public ArrayList<Symbol> findMembers(String name )
    {
        ArrayList<Symbol> arrayList = new ArrayList<>();
        /* 第1步:搜索字段 */
        BVarSymbol fieldSymbol =this.findField(name);
        if(fieldSymbol!=null )
            arrayList.add(fieldSymbol);
        /* 第1步:搜索方法 */
        Method[] methods = clazz.getMethods();
        for(Method method :methods)
        {
            if(method.getName().equals(name) && Modifier.isPublic(method.getModifiers()))
            {
                RMethodSymbol methodSymbol = RMethodSymbolManager.getSymbol(method,this);
                arrayList.add(methodSymbol);
            }
        }
        return arrayList;
    }
}
