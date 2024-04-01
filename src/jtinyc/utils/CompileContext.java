package jtinyc.utils;

import jtinyc.symbols.DSourceFileSymbol;
import java.util.ArrayList;

public class CompileContext {

    public int errors = 0;
    //public int warnings = 0;

    public SimpleLog log = new SimpleLog(this);

    public ArrayList<DSourceFileSymbol> fileSymbols = new ArrayList<>();
}
