package calculator.ast;

/* 括号表达式语法树 : ( expr) */
public class BracketsExpr extends Expr {
    public Expr innerExpr;

    public BracketsExpr(Expr innerExpr)
    {
        this.innerExpr=innerExpr;
    }

    @Override
    public String toString()
    {
        return "("+innerExpr.toString()+")";
    }
}
