package calculator.ast;

/* double数值语法树*/
public class DoubleValueExpr extends Expr {
    public String doubleText; //数组的字符串形式
    public DoubleValueExpr(String intText)
    {
        this.doubleText=intText;
    }

    @Override
    public String toString()
    {
        return doubleText;
    }
}
