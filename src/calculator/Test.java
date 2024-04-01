package calculator;

/* 方便开发测试用 */
class Test {
    public static void main(String[] args) throws CalcException
    {
        String code ="";
        code="47.0";
        code="1+2";
        code="()";
        code ="9*(2+3)/6";
     //   code = "1*2/3*4/6";
       // code = "1+2+3+4";
        try {
            System.out.println("编译表达式:"+code);
            CalcMain. compileRun(code,true);
        } catch (CalcException ce) {
            System.err.println(ce.getMessage());
        }
    }
}
