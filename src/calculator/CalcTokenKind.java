package calculator;

/* 词类型枚举*/
public enum CalcTokenKind {
    DoubleLiteral, //double值
    LeftBracket, //左括号'('
    RightBracket, //右括号')'
    ADD, // 加法运算: +
    SUB, // 减法运算: -
    MUL,// 乘法运算: *
    DIV,  // 除法运算: /
    EOL, // 表达式结尾
    Error //错误
}
