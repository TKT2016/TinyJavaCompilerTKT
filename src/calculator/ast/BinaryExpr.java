package calculator.ast;
import calculator.CalcToken;
/* 二元运算语法树 */
public class BinaryExpr extends Expr {
    public Expr left; //左表达式
    public Expr right; //右表达式
    public CalcToken opToken; //运算符

    public BinaryExpr(Expr left, Expr right, CalcToken opToken)
    {
        this.left=left;
        this.right=right;
        this.opToken=opToken;
    }

    @Override
    public String toString()
    {
        return left.toString()+opToken.toString()+right.toString();
    }
}