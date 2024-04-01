package calculator;
/*编译异常，用于报错*/
public class CalcException extends Exception{
    public CalcException(String msg) {
        super("语法错误: " + msg);
    }

    public CalcException(String msg, CalcToken t) {
        super(getMessage(msg,t));
    }

    private static String getMessage(String msg, CalcToken t)
    {
        if(t==null)
            return  msg;
        else  if(t.kind== CalcTokenKind.EOL)
            return "意外结尾,位置:第" + (t.pos+1) + "列:" + msg;
        else
            return "语法错误,位置:第" + (t.pos+1) + "列:" + msg+":"+t.text.toString();
    }
}
