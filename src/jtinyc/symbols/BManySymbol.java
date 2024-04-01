package jtinyc.symbols;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/** 多个重复符号 */
public class BManySymbol extends Symbol {

    public ArrayList<Symbol> symbols;

    public BManySymbol(String name, ArrayList<Symbol> symbols)
    {
       super(name);
        this.symbols =symbols;
    }
/*
    public ArrayList<BMethodSymbol> matchArgTypes(ArrayList<BTypeSymbol> argTypes)
    {
        ArrayList<BMethodSymbol> symbolsMatchs = new ArrayList<>();
        int min = -1 ;
        Map<BMethodSymbol,Integer> map =new HashMap<>();

        for(Symbol symbol:symbols)
        {
            if(symbol instanceof BMethodSymbol)
            {
                BMethodSymbol methodSymbol = (BMethodSymbol)symbol;
                int k = methodSymbol.matchArgTypes(argTypes);
                if(k>=0)
                {
                    if(min==-1)
                        min=k;
                    else
                        min = Math.min(min,k);
                    symbolsMatchs.add(methodSymbol);
                    map.put(methodSymbol,k);
                }
            }
        }
        if(symbolsMatchs.size()<=1)
            return symbolsMatchs;

        ArrayList<BMethodSymbol> symbolsMatchs2 = new ArrayList<>();
        for(BMethodSymbol symbol:map.keySet())
        {
            Integer value = map.get(symbol);
            if(value.intValue()==min)
                symbolsMatchs2.add(symbol);
        }

        if(symbolsMatchs2.size()<=1)
            return symbolsMatchs2;
*/
        /* 根据返回值判断 *//*
        ArrayList<BMethodSymbol> symbolsMatchs3 = new ArrayList<>();
        int deep = -1;

        for(BMethodSymbol symbol:symbolsMatchs2)
        {
            int symboldeep = SymbolUtil.getExtendsDeep(symbol.returnType);
            if(symboldeep==deep)
            {
                symbolsMatchs3.add(symbol);
            }
            else if(symboldeep>deep)
            {
                symbolsMatchs3.clear();
                symbolsMatchs3.add(symbol);
                deep = symboldeep;
            }
        }
        return symbolsMatchs3;
    }*/
}
