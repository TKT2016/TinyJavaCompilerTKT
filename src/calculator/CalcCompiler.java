package calculator;

import calculator.ast.Expr;

/* 四则运算编译器，把词法分析器、语法分析器、语义分析器、字节码生成器组合在一起 */
public class CalcCompiler
{
    CalcGenerator generator = new CalcGenerator();

    public void compile(String str) throws CalcException
    {
        if(str==null)
            throw new CalcException("表达式为null");
        CalcLexer lexer = new CalcLexer(str);
        CalcParser parser = new CalcParser(lexer);
        Expr expr = parser.parse();
        (new CalcAnalysis()).analyExpr(expr);
        generator.emitClass(expr);
    }
}
