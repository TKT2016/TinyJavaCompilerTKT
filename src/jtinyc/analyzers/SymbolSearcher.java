/*
 * Jtiny compiler source code
 * License : the GNU General Public License
 * Copyright (C) 2021-2024 Chines JSSQSY WANG
 */
package jtinyc.analyzers;

import jtinyc.lex.TokenKind;
import jtinyc.symbols.*;
import jtinyc.trees.*;
import jtinyc.utils.CompileError;
import jtinyc.utils.Debuger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SymbolSearcher {

    /* 根据词法类型查找符号 */
    public static RClassSymbol getPrimitiveSymbol(TokenKind tokenKind)
    {
        switch (tokenKind) {
            case BOOLEAN:
            case TRUE:
            case FALSE:
                return  RClassSymbolManager.booleanPrimitiveSymbol;
            case INT:
            case INTLITERAL:
                return RClassSymbolManager.intPrimitiveSymbol;
            case VOID:
                return RClassSymbolManager.voidPrimitiveSymbol;
            case STRINGLITERAL:
                return RClassSymbolManager.StringSymbol;
            default:
                throw new CompileError();
        }
    }

    /* 查找JCPrimitiveTypeTree的类型符号 */
    public static RClassSymbol findType(JCPrimitiveType jcPrimitiveType)
    {
        RClassSymbol classSymbol = getPrimitiveSymbol(jcPrimitiveType.kind);
        /* 顺便给JCPrimitiveTypeTree的symbol赋值,减少工作量*/
        jcPrimitiveType.symbol =classSymbol;
        return classSymbol;
    }

    /* 查找JCFieldAccess类型符号 */
    public static RClassSymbol findType(JCFieldAccess jcFieldAccess)
    {
        /* 取得JCFieldAccess的全名 */
        String fullName =AnalyzerUtil.fullName(jcFieldAccess);
        /*使用反射从类名称获取Class符号*/
        RClassSymbol classSymbol = RClassSymbolManager.forName(fullName);
        if(classSymbol ==null) {
            jcFieldAccess.log.error(jcFieldAccess.nameToken  , String.format("类型'%s'不存在", fullName));
            jcFieldAccess.symbol = new BErroneousSymbol();
        }
        else
             jcFieldAccess.symbol =classSymbol;
        return classSymbol;
    }

    /* 查找标识符的类型符号 */
    public static BTypeSymbol findType(JCIdent jcIdent , SymbolScope scope)
    {
        String typeName = jcIdent.getName();
        ArrayList<BTypeSymbol> typeSymbols = findTypes(typeName,scope);
        if (typeSymbols.size() == 1) {
            jcIdent.symbol= typeSymbols.get(0);
        }
        else if (typeSymbols.size() == 0) {
            jcIdent.error(String.format("找不到类型'%s'",typeName));
            jcIdent.symbol = RClassSymbolManager.ObjectSymbol;// new BErroneousSymbol();
        }
        else {
            jcIdent.error(String.format("对'%s'的引用不明确",typeName));
            jcIdent.symbol= RClassSymbolManager.ObjectSymbol;// new BErroneousSymbol();
        }
        return jcIdent.symbol.getTypeSymbol();
    }

    /* 根据名称递归查找所有类型符号 */
    public static ArrayList<BTypeSymbol> findTypes( String typeName,SymbolScope scope) {
        SymbolScope temp = scope;
        ArrayList<BTypeSymbol> list = new ArrayList<>();
        while (temp != null) {
            ArrayList<BTypeSymbol> typeSymbols = findScopeTypes(temp,typeName);
            if (typeSymbols != null && typeSymbols.size() > 0)
                list.addAll(typeSymbols);
            temp = temp.parent;
        }
        return list;
    }

    /** 从作用域查找变量 */
    public static ArrayList<BTypeSymbol> findScopeTypes(SymbolScope scope,String varName) {
        if (scope.symbolMap.containsKey(varName)) {
            ArrayList<Symbol> symbols2 = scope.symbolMap.get(varName);
            return SymbolUtil.filterTypeSymbols(symbols2);
        }
        return null;
    }

    /** 在作用域中查找数组的类型符号 */
  /*  public static BArrayTypeSymbol findType(JCArrayTypeTree arrayTypeTree, SymbolScope scope)
    {
        BTypeSymbol eleType = findType(arrayTypeTree.elemType,scope);
        BArrayTypeSymbol arrayTypeSymbol = BArrayTypeSymbol.getSymbol(eleType);
        arrayTypeTree.symbol = arrayTypeSymbol;
        return arrayTypeSymbol;
    }*/

    /** 查找表达式类型符号 */
    public static BTypeSymbol findType(JCExpression exp , SymbolScope scope) {
        if(exp.symbol!=null)
            return exp.symbol.getTypeSymbol();

        if (exp instanceof JCPrimitiveType) {
            return findType( (JCPrimitiveType)exp);
        }
        else if (exp instanceof JCFieldAccess) {
            return findType( (JCFieldAccess)exp);
        }
        else if (exp instanceof JCIdent) {
            return findType( (JCIdent)exp,scope);
        }
        /*else if (exp instanceof JCArrayTypeTree) {
            return findType( (JCArrayTypeTree)exp,scope);
        }*/
        else {
            throw new CompileError();
        }
    }
    /*
    public static List<Symbol> findVars(SymbolScope scope, String name )
    {
        ArrayList<Symbol>  symbols = findIdents(scope, name );
        List<Symbol> varSybmols = symbols.stream().filter(A->A instanceof BVarSymbol).collect(Collectors.toList());
        return varSybmols;
    }*/

    /** 搜索变量符号,从作用域中搜索，并递归自底向上搜索符号条件的符号 */
    public static ArrayList<Symbol> findIdents(SymbolScope scope, String name)
    {
        ArrayList<Symbol> list = new ArrayList<>();
        SymbolScope temp = scope;
        while (temp!=null)
        {
            ArrayList<Symbol> finds = temp.symbolMap.get(name);
            list.addAll(finds);
            temp = temp.parent;
        }
        return list;
    }

    /** 从作用域中搜索自底向上并递归搜索符号条件的符号 */
    //public static ArrayList<Symbol> findVars( SymbolScope scope, String varName /*, IdentSearchKinds searchKinds*/) {
        //ArrayList<Symbol> list = new ArrayList<>();
        //ArrayList<Symbol> symbols2 = scope.symbolMap.get(varName);
        //if (searchKinds.isSearchVar) {
        //    ArrayList<Symbol> founds1 = SymbolUtil.filterVarSymbols(symbols2);
       //     list.addAll(founds1);
       // }

        //if (searchKinds.isSearchType) {
        //    ArrayList<BTypeSymbol> founds2 = SymbolUtil.filterTypeSymbols(symbols2);
        //    list.addAll(founds2);
        // }
        //if (searchKinds.isSearchFunc) {
        //    ArrayList<Symbol> founds3 = SymbolUtil.filterMethodSymbols(symbols2);
        //    list.addAll(founds3);
        //}
       // return list;
    //}

    /* 验证函数符号参数与指定的参数类型匹配度 */
    public static int matchMethod(BMethodSymbol methodSymbol, ArrayList<BTypeSymbol> argTypes)
    {
        int paramCount = methodSymbol.getParameterCount();
        // 1: 比较参数个数是否相同
        if(argTypes.size()!=paramCount)
            return -1;
        //2 : 如果参数个数都为0，则都是匹配的
        if(paramCount==0)
            return 0;
        else {
            // 比较每个参数的匹配度，并累加；但是只要有一个参数不匹配，则这些函数都是不匹配的，直接返回-1
            int sum = 0;
            for (int i = 0; i < paramCount; i++) {
                BTypeSymbol argtypeSymbol = argTypes.get(i);
                BTypeSymbol paramSymbol = methodSymbol.getParameterSymbol(i).getTypeSymbol();
                int k = SymbolUtil.matchAssignableSymbol(paramSymbol, argtypeSymbol);
                if (k < 0)
                    return -1;
                else
                    sum += k;
            }
            return sum;
        }
    }

    /** 根据参数类型和返回值过滤多个函数符号 */
    public static ArrayList<BMethodSymbol> filterMethods(BManySymbol manySymbol , ArrayList<BTypeSymbol> argTypes)
    {
        /* 1 筛选出函数符号 */
        ArrayList<BMethodSymbol> methods1 = new   ArrayList<BMethodSymbol>();
        for (Symbol symbol : manySymbol.symbols) {
            if(symbol instanceof  BMethodSymbol)
            {
                BMethodSymbol methodSymbol = (BMethodSymbol) symbol;
                methods1.add(methodSymbol);
            }
        }
        if(methods1.size()<=1)
            return methods1;
        /* 根据参数类型匹配度筛选 */
        ArrayList<BMethodSymbol> methods2 = new MethodsFilterByArgsTypes(methods1,argTypes).filter();
        if(methods2.size()<=1)
            return methods2;
        /* 根据函数返回类型匹配度筛选 */
        ArrayList<BMethodSymbol> methods3 = new MethodsFilterByReturnType(methods2).filter();
        return methods3;
    }

    /* 筛选父类 */
    static  abstract class MethodsFilter
    {
        abstract  ArrayList<BMethodSymbol> getMethods();
        abstract int getRate(BMethodSymbol methodSymbol);

        public  ArrayList<BMethodSymbol> filter()
        {
            Integer minRate =null;
            Map<BMethodSymbol, Integer> map = new HashMap<>();
            for (Symbol symbol : getMethods()) {
                BMethodSymbol methodSymbol = (BMethodSymbol) symbol;
                int rate =getRate(methodSymbol);
                if(rate>=0)
                {
                    map.put(methodSymbol,rate);
                    if(minRate==null)
                        minRate = rate;
                    else
                        minRate = Math.min(minRate,rate);
                }
            }
            ArrayList<BMethodSymbol> methodSymbolsNew = new ArrayList<>();
            for (BMethodSymbol methodSymbol : map.keySet()) {
                Integer rate = map.get(methodSymbol);
                if(rate==minRate)
                    methodSymbolsNew.add(methodSymbol);
            }
            return methodSymbolsNew;
        }
    }
    /** 根据函数参数类型筛选 */
   static  class MethodsFilterByArgsTypes extends MethodsFilter
    {
        ArrayList<BMethodSymbol> methods;
        ArrayList<BTypeSymbol> argTypes;

        public MethodsFilterByArgsTypes(ArrayList<BMethodSymbol> methods, ArrayList<BTypeSymbol> argTypes)
        {
            this. methods = methods;
            this.argTypes=argTypes;
        }
          ArrayList<BMethodSymbol> getMethods()
          {
                return methods;
          }

         int getRate(BMethodSymbol methodSymbol)
         {
            return matchMethod(methodSymbol, argTypes);
         }
    }

    /** 根据函数返回类型筛选 */
    static class MethodsFilterByReturnType extends MethodsFilter
    {
        ArrayList<BMethodSymbol> methods;
        public MethodsFilterByReturnType(ArrayList<BMethodSymbol> methods)
        {
            this.methods=methods;
        }

        ArrayList<BMethodSymbol> getMethods()
        {
            return methods;
        }

        int getRate(BMethodSymbol methodSymbol)
        {
            return SymbolUtil.getExtendsDeep(methodSymbol.returnType);
        }
    }
}
