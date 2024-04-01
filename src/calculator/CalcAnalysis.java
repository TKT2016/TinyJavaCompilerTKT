package calculator;

import calculator.ast.BinaryExpr;
import calculator.ast.BracketsExpr;
import calculator.ast.DoubleValueExpr;
import calculator.ast.Expr;

/* 语义分析器 */
public class CalcAnalysis {

    public void analyExpr(Expr expr)  throws CalcException
    {
        if(expr instanceof DoubleValueExpr)
        {
            analyDouble((DoubleValueExpr)expr);
        }
        else if(expr instanceof BracketsExpr)
        {
            analyBrackets((BracketsExpr)expr);
        }
        else if(expr instanceof BinaryExpr)
        {
            eanalyBinary((BinaryExpr)expr);
        }
    }

    private void analyDouble(DoubleValueExpr expr)  throws CalcException
    {
        try
        {
            Double.parseDouble(expr.doubleText);
        }
        catch(NumberFormatException ex){
            throw new CalcException(expr.doubleText+"不是数字");
        }
    }

    private void eanalyBinary(BinaryExpr binaryExpr)  throws CalcException
    {
        analyExpr(binaryExpr.left);
        analyExpr(binaryExpr.right);
    }

    private void analyBrackets(BracketsExpr bracketsExpr) throws CalcException
    {
        if(bracketsExpr.innerExpr==null)
            throw new CalcException("括号内没有表达式");
        else
            analyExpr(bracketsExpr.innerExpr);
    }
}
