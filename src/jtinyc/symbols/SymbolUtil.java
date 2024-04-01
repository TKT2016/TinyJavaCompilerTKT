package jtinyc.symbols;

import jtinyc.utils.CompileError;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

/** 符号辅助类 */
public class SymbolUtil {

    /** 获取符号的类型 */
    public static BTypeSymbol getSymbolType(Symbol symbol)
    {
        if(symbol instanceof BTypeSymbol)
            return (BTypeSymbol)symbol;

        /* 如果是错误符号,返回Object类型 */
         if(symbol instanceof BErroneousSymbol)
            return RClassSymbolManager.ObjectSymbol ;

        /* 如果是多个难以有歧义的类型，返回Object类型*/
        if(symbol instanceof BManySymbol)
            return RClassSymbolManager.ObjectSymbol;

        /*如果是变量符号，返回变量符号的类型*/
        if(symbol instanceof BVarSymbol)
        {
            BVarSymbol varSymbol=(BVarSymbol)symbol;
            return  varSymbol.varType;
        }
        /*如果是方法符号，返回方法的返回类型 */
        if(symbol instanceof BMethodSymbol)
        {
            BMethodSymbol methodSymbol=(BMethodSymbol)symbol;
            return  methodSymbol.returnType;
        }

        /*其它情况，说明编译器有bug，抛出错误以便修正*/
        throw new CompileError();
    }

/*
    public static  Class<?> getClassSymbolClass(Symbol symbol)
    {
        RClassSymbol rClassSymbol = (RClassSymbol) symbol;
        return  rClassSymbol.clazz;
    }*/

    public static boolean isStatic(Symbol symbol)
    {
        if(symbol instanceof RVarSymbol)
        {
            RVarSymbol varSymbol=(RVarSymbol)symbol;
            return  varSymbol.isStatic;
        }
        if(symbol instanceof RMethodSymbol)
        {
            RMethodSymbol tsymbol=(RMethodSymbol)symbol;
            return  tsymbol.isStatic;
        }
        if(symbol instanceof DMethodSymbol)
        {
            return true;
        }
        return false;
    }

/*
    public static BTypeSymbol getElementType(Symbol symbol)
    {
        BTypeSymbol typeSymbol =symbol.getTypeSymbol();
        if(typeSymbol instanceof BArrayTypeSymbol)
        {
            return ((BArrayTypeSymbol)typeSymbol).elementType;
        }
        return null;
    }*/

    public static ArrayList<Symbol> filterVarSymbols(ArrayList<Symbol> list)
    {
        ArrayList<Symbol> arrayList = new ArrayList<>();
        for (Symbol t:list)
        {
            if(t instanceof BVarSymbol)
            {
                arrayList.add(t);
            }
        }
        return arrayList;
    }
/*
    public static ArrayList<Symbol> filterMethodSymbols(ArrayList<Symbol> list)
    {
        ArrayList<Symbol> arrayList = new ArrayList<>();
        for (Symbol t:list)
        {
            if(t instanceof BMethodSymbol)
            {
                arrayList.add(t);
            }
        }
        return arrayList;
    }*/

    public static ArrayList<BTypeSymbol> filterTypeSymbols(ArrayList<Symbol> list)
    {
        ArrayList<BTypeSymbol> arrayList = new ArrayList<>();
        for (Symbol t:list)
        {
            if(t instanceof BTypeSymbol)
            {
                arrayList.add((BTypeSymbol) t);
            }
        }
        return arrayList;
    }

    public static int getExtendsDeep(BTypeSymbol typeSymbol)
    {
        if(typeSymbol instanceof RClassSymbol)
        {
            return getExtendsDeep(((RClassSymbol)typeSymbol).clazz);
        }
        else
        {
            return 1;
        }
    }

    public static int getExtendsDeep(Class<?> clazz)
    {
        int i=0;
        Class<?> temp = clazz;
        while (temp!=null)
        {
            i++;
            temp=temp.getSuperclass();
        }
        return i;
    }

/*
    public static boolean isDeclFiledSymbol(BSymbol symbol)
    {
        if(symbol instanceof DVarSymbol)
        {
            DVarSymbol declVarSymbol =(DVarSymbol)symbol;
            if(declVarSymbol.varKind == VarSymbolKind.field)
                return true;
        }
        return false;
    }*/

    public static BTypeSymbol getOwnerType(Symbol symbol)
    {
        if(symbol instanceof BVarSymbol)
        {
            BVarSymbol varSymbol =(BVarSymbol)symbol;
            return varSymbol.ownerType;
        }
        else if(symbol instanceof BMethodSymbol)
        {
            BMethodSymbol methodSymbol =(BMethodSymbol)symbol;
            return methodSymbol.ownerType;
        }
        return null;
    }

    //public static boolean isArrayLengthSymbol(Symbol symbol)
 //   {
  //      return  symbol.equals(BArrayTypeSymbol.ArrayLengthFieldSymbol);
       /* if(!(symbol instanceof BVarSymbol)) return false;
        BVarSymbol varSymbol =(BVarSymbol) symbol;
        if(varSymbol.name.equals( NamesTexts.length) &&varSymbol.ownerType==null )
            return true;
        return false;*/
 //   }

    /** 根据参数类型列表查找构造函数 */
    public static ArrayList<BMethodSymbol> findConstructor(Symbol symbol, ArrayList<BTypeSymbol> argTypes) {
        if (symbol instanceof RClassSymbol) {
            RClassSymbol classSymbol = (RClassSymbol) symbol;
            Constructor[] constructors = classSymbol.clazz.getConstructors();
            ArrayList<BMethodSymbol> methodSyms = new ArrayList<>();
            for (Constructor constructor : constructors) {
                if (Modifier.isPublic(constructor.getModifiers())) {
                    Class<?>[] parameterTypes = constructor.getParameterTypes();
                    if (isAssignFrom(parameterTypes, argTypes) !=-1) {
                        RMethodSymbol methodSymbol = RMethodSymbolManager.getSymbol(constructor, classSymbol);
                        methodSyms.add(methodSymbol);
                    }
                }
            }
            return methodSyms;
        }
        return null;
    }

    public static int isAssignFrom(Class<?>[] parameterTypes, ArrayList<BTypeSymbol> argTypes) {
        if (parameterTypes.length != argTypes.size())
            return -1;
        int sum=0;
        for (int i = 0; i < parameterTypes.length; i++) {
            BTypeSymbol typeSymbol = RClassSymbolManager.getSymbol(parameterTypes[i]);
            int v = matchAssignableSymbol(typeSymbol,argTypes.get(i));
            if(v==-1)
                return -1;
            else
                sum+=v;
            /*if (!typeSymbol.isAssignableFromRight(argTypes.get(i))) {
                return 0;
            }*/
        }
        return sum;
    }

    /** 判断符号是否是基本类型的 */
  /*  public static boolean isPrimitive(Symbol symbol)
    {
        if(!(symbol instanceof RClassSymbol)) return false;
        RClassSymbol rClassSymbol = (RClassSymbol)symbol;
        return rClassSymbol.clazz.equals(boolean.class) || rClassSymbol.clazz.equals(int.class)|| rClassSymbol.clazz.equals(double.class);
    }*/

    public static boolean isBoolean(Symbol symbol)
    {
        return symbol.equals(RClassSymbolManager.booleanPrimitiveSymbol);
    }

    public static boolean isString(Symbol symbol)
    {
        return symbol.equals(RClassSymbolManager.StringSymbol);
    }

    public static boolean isInt(Symbol symbol)
    {
        return symbol.equals(RClassSymbolManager.intPrimitiveSymbol);
    }

    public static boolean isVoid(Symbol symbol)
    {
        return symbol.equals(RClassSymbolManager.voidPrimitiveSymbol);
    }

    /** 计算符号的匹配度  */
    public static int matchAssignableSymbol(BTypeSymbol leftSymbol,BTypeSymbol rightSymbol)
    {
        if(leftSymbol.equals(rightSymbol)) return  0;
       /* if(leftSymbol instanceof BArrayTypeSymbol)
        {
            return -1;
        }
        else*/ if(leftSymbol instanceof  BErroneousSymbol)
        {
            return 1;
        }
        else if(leftSymbol instanceof DSourceFileSymbol)
        {
            return -1;
        }
        else if(leftSymbol instanceof RClassSymbol)
        {
            RClassSymbol ca = (RClassSymbol) leftSymbol;
           /* if(rightSymbol instanceof BArrayTypeSymbol)
            {
                if(isObject(ca)) return 1;
                else
                    return -1;
            }
            else*/ if(rightSymbol instanceof  BErroneousSymbol)
            {
                return 1;
            }
            else  if(rightSymbol instanceof DSourceFileSymbol)
            {
                if(isObject(ca)) return 1;
                else
                    return -1;
            }
            else if(rightSymbol instanceof RClassSymbol)
            {
                RClassSymbol cb = (RClassSymbol) rightSymbol;
                return matchAssignableClass(ca.clazz,cb.clazz);
            }
            throw new CompileError();
        }
        throw new CompileError();
    }

    /** 计算a赋值为b的匹配度(-1:不匹配,0:相同,其它数值:继承多少层) */
    public static int matchAssignableClass(Class<?> a,Class<?> b)
    {
        if(a.equals(b)) return 0;
        if(! a.isAssignableFrom((b))) return -1;
        if(b.isInterface()) return 1;

        int i= 0 ;
        Class<?> temp =b;
        while (temp!=null)
        {
            if(a .equals(temp))
                break;
            temp = temp.getSuperclass();
            i++;
        }
        return i;
    }

    /** 是否是Object类型 */
    public static boolean isObject(BTypeSymbol symbol)
    {
        if(symbol instanceof RClassSymbol)
        {
            RClassSymbol classSymbol = (RClassSymbol) symbol;
            return classSymbol.clazz.equals(Object.class);
        }
        return false;
    }
}
