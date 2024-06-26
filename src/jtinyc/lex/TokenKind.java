package jtinyc.lex;
/** 词标记类型*/
public enum TokenKind
{
    EOF(), //结束符号
    ERROR(), //错误
    IDENTIFIER(), //标识符
    INTLITERAL(), //整数常量类型
    STRINGLITERAL(), //字符串常量类型
    BOOLEAN("boolean"),
    ELSE("else"),
    FOR("for"),
    IF("if"),
    IMPORT("import"),
    INT("int"),
    //NEW("new"),
    PACKAGE("package"),
    RETURN("return"),
    VOID("void"),
    WHILE("while"),
    TRUE("true"),
    FALSE("false"),
    LPAREN("("),
    RPAREN(")"),
    LBRACE("{"),
    RBRACE("}"),
    //LBRACKET("["),
    //RBRACKET("]"),
    SEMI(";"),
    COMMA(","),
    DOT("."),
    EQ("="),
    GT(">"),
    LT("<"),
    NOT("!"),
    EQEQ("=="),
    LTEQ("<="),
    GTEQ(">="),
    NOTEQ("!="),
    AND("&&"),
    OR("||"),
    ADD("+"),
    SUB("-"),
    MUL("*"),
    DIV("/")
    ;
    /* 类型对应的名称 */
    public final String name;
    TokenKind() {
        this(null);
    }
    TokenKind(String name) {
        this.name = name;
    }

    public String toString() {
        switch (this) {
            case IDENTIFIER:
                return "token.identifier";
            case STRINGLITERAL:
                return "token.string";
            case INTLITERAL:
                return "token.integer";
            case ERROR:
                return "token.error";
            case EOF:
                return "token.end-of-input";
            case DOT: case COMMA: case SEMI:
            case LPAREN: case RPAREN:
            //case LBRACKET: case RBRACKET:
            case LBRACE: case RBRACE:
                return "'" + name + "'";
            default:
                return name;
        }
    }
}
