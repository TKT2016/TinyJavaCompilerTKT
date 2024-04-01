package calculator;

/* 词标记类 */
public class CalcToken {
    public final CalcTokenKind kind; //类型
    public final int pos; //位置
    public final String text;

    public CalcToken(CalcTokenKind kind, String text, int pos)
    {
        this.kind=kind;
        this.text=text;
        this.pos=pos;
    }

    @Override
    public String toString()
    {
        switch (kind)
        {
            case DoubleLiteral:
                return text;
            case LeftBracket:
                return "(";
            case RightBracket:
                return ")";
            case ADD:
                return "+";
            case SUB:
                return "-";
            case MUL:
                return "*";
            case DIV:
                return "/";
            case EOL:
                return "(END)";
            default:
                return "";
        }
    }
}
