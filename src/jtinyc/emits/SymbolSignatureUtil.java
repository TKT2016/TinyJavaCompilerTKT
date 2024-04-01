package jtinyc.emits;

import jtinyc.symbols.*;
/** 签名生成 */
public abstract class SymbolSignatureUtil {

    public static String getParamsSignature(DSourceFileSymbol symbol, boolean insertL)
    {
        String lstr = insertL?"L":"";
        String fullName = symbol.getFullname();
        String str = fullName.replace(".", "/");
        String elstr = insertL?";":"";
        return lstr +str+elstr;
    }

    public static String getParamsSignature(Symbol symbol, boolean insertL)
    {
        if(symbol instanceof DSourceFileSymbol)
            return getParamsSignature((DSourceFileSymbol) symbol,  insertL);
        else if(symbol instanceof BMethodSymbol)
            return getParamsSignature((BMethodSymbol) symbol,  insertL);
        else if(symbol instanceof RClassSymbol)
            return getParamsSignature((RClassSymbol) symbol,  insertL);
        /*else if(symbol instanceof BArrayTypeSymbol)
            return getParamsSignature((BArrayTypeSymbol) symbol,  insertL);*/
        else if(symbol instanceof BErroneousSymbol)
            return getParamsSignature( RClassSymbolManager.ObjectSymbol,  insertL);
        else
            return getParamsSignature(symbol.getTypeSymbol(),insertL);
    }

/*    public  static  String getParamsSignature(BArrayTypeSymbol symbol, boolean insertL)
    {
        return "["+ getParamsSignature(symbol.elementType,insertL);// return "["+ elementType.getSignature(insertL);
    }*/

    public  static  String getParamsSignature(RClassSymbol symbol, boolean insertL)
    {
        return getParamsSignature(symbol.clazz,insertL);
    }

    /**  生成函数参数列表及返回值的签名 */
    public  static  String getParamsSignature(BMethodSymbol symbol, boolean insertL)
    {
        final StringBuffer buf = new StringBuffer();
        buf.append("(");
        for (int i = 0; i < symbol.getParameterCount(); i++) {
            Symbol symboli =  symbol.getParameterSymbol(i);
            BTypeSymbol typeSymboli = symboli.getTypeSymbol();
            if(typeSymboli!=null) {
                String singature = getParamsSignature(typeSymboli,(true));
                buf.append(singature);
            }
        }
        buf.append(")");
        if( symbol.isConstructor)
            buf.append("V");
        else
            buf.append( getParamsSignature( symbol.returnType,(insertL)));
        return buf.toString();
    }

    public static String getParamsSignature(Class<?> clazz, boolean insertL)
    {
        if (int.class.equals(clazz)) {
            return "I";
        }
        else if (void.class.equals(clazz)) {
            return "V";
        }
        else if (boolean.class.equals(clazz)) {
            return "Z";
        }
        else if (char.class.equals(clazz)) {
            return "C";
        }
        else  if (byte.class.equals(clazz)) {
            return "B";
        }
        else if (short.class.equals(clazz)) {
            return "S";
        }
        else if (float.class.equals(clazz)) {
            return "F";
        }
        else  if (long.class.equals(clazz)) {
            return "J";
        }
        else  if (double.class.equals(clazz)) {
            return "D";
        }
        else
        {
            String nameFull = clazz.getName();
            String name2 = nameFull.replaceAll("\\.", "/");
            if(insertL &&! name2.startsWith("["))
                return warpL(name2);
            else
                return name2;
           /* String nameFull = clazz.getName();
            String name2 = nameFull.replaceAll("\\.", "/");
            String lstr = insertL?"L":"";
            String elstr = insertL?";":"";
            return lstr + name2+elstr;*/
        }
    }

    public static String warpL(String sign  )
    {
        if(sign.startsWith("["))
            return sign;
        else
            return "L" + sign+";";
    }
    public static String nameToSign(String classFullName)
    {
        String  str = classFullName.replace(".", "/");
        return str;
    }
}
