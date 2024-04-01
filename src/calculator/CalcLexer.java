package calculator;

/* 词法分析器 */
public class CalcLexer {
    private char ch; //当前字符
    private int pos ; //当前位置
    private final int size; //最大长度
    private final String src; //表达式源码

    /* 构造函数 */
    public CalcLexer(String src) throws CalcException
    {
        if(src.length()==0)
            throw new CalcException("表达式长度为0");
        /* 初始化*/
        this.src=src;
        size = src.length();
        pos = 0; //当前位置要设为0
        readChar(); //必须给当前字符赋值，才能进行扫描
    }

    /* 根据当前位置从表达式源码中给当前字符赋值 */
    private void readChar() {
        if(pos >=size) {
            ch = '\0';
        }
        else {
            ch = src.charAt(pos);
        }
    }

    /* 移动到下一个位置并读取字符 */
    private void next()
    {
        pos++;
        readChar();
    }

    /* 扫描词法 */
    public CalcToken scan()  throws CalcException
    {
        /* 跳过空格 */
        if(ch==' '|| ch=='\t')
            skipSpace();

        CalcToken token = null;
        switch(ch) {
            case '+': //扫描加运算符
                token  =  new CalcToken(CalcTokenKind.ADD, "+", pos );
                next(); //本次状态结束，移动到下一个字符，方便下一次扫描
                return token;
            case '-':
                token  =  new CalcToken(CalcTokenKind.SUB, "-", pos );
                next();
                return token;
            case '*':
                token  =  new CalcToken(CalcTokenKind.MUL, "*", pos );
                next();
                return token;
            case '/':
                token  =  new CalcToken(CalcTokenKind.DIV, "-", pos );
                next();
                return token;
            case '(':
                token  = new CalcToken(CalcTokenKind.LeftBracket, "(", pos );
                next();
                return token;
            case ')':
                token  =  new CalcToken(CalcTokenKind.RightBracket, ")", pos );
                next();
                return token;
            case '0': case '1':case '2': case '3':case '4': case '5':case '6': case '7':case '8': case '9': //记录数值
                return scanNumber();
            case'\0':
                token =  new CalcToken(CalcTokenKind.EOL, "", pos );
                return token;
            default:
                token = new CalcToken(CalcTokenKind.Error,String.valueOf(ch), pos);
                next();
                throw new CalcException("无法识别字符", token);
        }
    }

    private CalcToken scanNumber() throws CalcException
    {
        int pos1 = this.pos;//记录数值开始位置
        StringBuffer stringBuffer = new StringBuffer();//用于记录数值字符串
        while( Character.isDigit(ch)||ch=='.' )
        {
            stringBuffer.append(ch);
            next();
        }
        String doubleText = stringBuffer.toString();
        return new CalcToken(CalcTokenKind.DoubleLiteral,doubleText, pos1 );
    }

    /*跳过空白 */
    private void skipSpace() throws CalcException
    {
        while (ch==' '|| ch=='\t')
            next();
    }
}
