package jtinyc.symbols;
import jtinyc.emits.SymbolSignatureUtil;
import java.util.*;
/** 源文件生成的类型符号 */
public class DSourceFileSymbol extends BTypeSymbol
{
    /** 定义的包名称 */
    public String packageName;

    /** 生成的class文件路径 */
    public String compiledClassFile;

    /** name值是源文件不带扩展名的名称 */
    public DSourceFileSymbol(String name) {
        super(name ,false , true);
    }

    /** 获取类型全名称 */
    public String getFullname()
    {
        String className = name;
        if(packageName==null)
            return className;
        if(packageName == null)
            return className;
        if(packageName.trim().equals(""))
            return className;
        return packageName+"."+className;
    }

    /** 源文件中定义的方法表 (方法签名->函数符号) */
    private final Map<String, DMethodSymbol> methods = new HashMap<>();

    /** 添加一个方法符号,如果签名已经存在,返回false */
    public boolean addMethod(DMethodSymbol methodSymbol)
    {
        /* 生成函数参数列表及返回值的签名(比如 [Ljava/lang/String;)V */
        String mtSign =  SymbolSignatureUtil.getParamsSignature(methodSymbol,true);
        /* 再加上函数名称 ,比如 main[Ljava/lang/String;)V */
        String sign =methodSymbol.name +mtSign;
        if(methods.containsKey(sign))
            return false;
        methods.put(sign,methodSymbol);
        return true;
    }

    /** 查找字段(没有字段)*/
    @Override
    public  BVarSymbol findField(String name)
    {
        return null;
    }
    /** 查找成员(只有方法)，这里查找名称相同的方法 */
    @Override
    public ArrayList<Symbol> findMembers(String name )
    {
        ArrayList<Symbol> symbols = new ArrayList<>();
        Iterator<Map.Entry<String, DMethodSymbol>> it = methods.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, DMethodSymbol> entry = it.next();
            DMethodSymbol methodSymbol = entry.getValue();
            if (methodSymbol.name.equals(name))
            {
                symbols.add(methodSymbol);
            }
        }
        return symbols;
    }
}
