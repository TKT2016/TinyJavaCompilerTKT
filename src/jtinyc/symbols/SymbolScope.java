package jtinyc.symbols;
import org.objectweb.asm.Label;
import tools.KeyValuesMap;
import java.util.ArrayList;

/** 作用域(包括代码块) */
public class SymbolScope
{
    public final SymbolScope parent;
    public ArrayList<SymbolScope> children = new ArrayList<>();
    public KeyValuesMap<String, Symbol> symbolMap = new KeyValuesMap<>();
    public SymbolScope(SymbolScope parent )
    {
        this.parent = parent;
    }

    public void addSymbol(Symbol symbol)
    {
        if(symbolMap.containsKey(symbol.name))
            return;
        symbolMap.put(symbol.name,symbol);
        if(symbol instanceof DVarSymbol)
        {
            DVarSymbol declVarSymbol =(DVarSymbol) symbol;
            declVarSymbol.scope = this;
        }
    }

    public SymbolScope createChild( )
    {
        SymbolScope blockSymbolFrame = new SymbolScope(this);
        this.children.add(blockSymbolFrame);
        return blockSymbolFrame;
    }

    public Label startLabel; //开始标签
    public Label endLabel; // 结束标签

    public void createLabels()
    {
        if(startLabel==null)
           startLabel = new Label();//开始标签
        if(endLabel==null)
            endLabel = new Label(); //结束标签
    }
}
