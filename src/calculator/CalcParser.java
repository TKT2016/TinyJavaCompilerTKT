package calculator;

import calculator.ast.*;

/* 语法分析器 */
public class CalcParser {
    private CalcLexer lex;   // 词法分析器
    private CalcToken tok;   // 当前token

    /* 构造函数 */
    public CalcParser(CalcLexer l) throws CalcException {
        lex = l;
        tok = lex.scan(); // 在开始时给当前token赋值
    }

    /* 词法分析器移动 */
    void move() throws CalcException {
        tok = lex.scan();
    }

    /* 匹配某个词 */
    void eat(CalcTokenKind t) throws CalcException
    {
        if( tok.kind == t )
            move();
        else
            error("语法错误:", tok);
    }

    /* 分析表达式语法树 */
    public Expr parse() throws CalcException {
        Expr expr = parseADDSUB();
        /* 在表达式分析完，还有多余的东西,表达式就是错误的，需要报错 */
        if(tok.kind!= CalcTokenKind.EOL)
        {
            throw new CalcException("应该是运算符,不应该是:"+ tok);
        }
        return expr;
    }

    /* '+'和'-'表达式分析 */
    Expr parseADDSUB()  throws CalcException {
        Expr left = parseMULDIV(); //调用优先级高一级的乘除分析
        System.out.println("parseADDSUB head:"+left); //讲解调试用，可以注释
        while( tok.kind == CalcTokenKind.ADD || tok.kind == CalcTokenKind.SUB ) {
            CalcToken op = this.tok; //局部变量记下运算符
            move(); //运算符已经处理完，移到一个词分析
            Expr right = parseMULDIV(); // 分析右表达式
            /* 以当前左右表达式和运算符创建二元运算表达式实例
             *  四则运算是从左向右的,所以把这个实例再赋值给left
             */
            left = new BinaryExpr(left,right,op);
            System.out.println("parseADDSUB left:"+left+" right:"+right);
        }
        return left;
    }

    /* '*'和'/'表达式分析 */
    Expr parseMULDIV()  throws CalcException
    {
        Expr left = term();
        System.out.println("parseMULDIV head:"+left); //讲解调试用，可以注释
        /* 当前是乘除运算符就一直循环分析乘除表达式 */
        while (tok.kind == CalcTokenKind.MUL || tok.kind == CalcTokenKind.DIV)
        {
            CalcToken op = this.tok; //局部变量记下运算符
            move();
            Expr right = term(); // 分析右表达式
            /* 以当前左右表达式和运算符创建二元运算表达式实例
             *  四则运算是从左向右的,所以把这个实例再赋值给left
             */
            left = new BinaryExpr(left, right, op);
            System.out.println("parseMULDIV left:"+left+" right:"+right);
        }
        return left;
    }

    /* 括号表达式和数值表达式分析 */
    Expr term()  throws CalcException
    {
        Expr x ;
        switch( tok.kind )
        {
            case LeftBracket:
                x =parseBracket();
                return x;
            case DoubleLiteral:
                x = number();
                return x;
            default:
                error("分析错误", tok);
                return null;
        }
    }

    /* 括号表达式分析 */
    Expr parseBracket() throws CalcException
    {
        eat(CalcTokenKind.LeftBracket); //吃掉左括号
        Expr x = parseBracketInner(); //分析括号内表达式
        eat(CalcTokenKind.RightBracket);//吃掉右括号
        BracketsExpr bracketsExpr = new BracketsExpr(x); //创建括号表达式实例
        return bracketsExpr;
    }

    /* 分析表达式语法树,和parse方法相比不需要检查末尾 */
    public Expr parseBracketInner() throws CalcException {
        Expr expr = parseADDSUB();
        return expr;
    }

    /* 数值表达式分析 */
    Expr number()  throws CalcException
    {
        DoubleValueExpr expr= new DoubleValueExpr(tok.text);
        move();
        return expr;
    }

    /* 提示错误 */
    void error(String s, CalcToken t) throws CalcException {
        throw new CalcException(s,t);
    }
}