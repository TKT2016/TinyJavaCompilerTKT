package calculator;

import tools.runs.ClassRunArg;
import tools.runs.ClassRuner;
import org.objectweb.asm.ClassWriter;
/* 编译运行表达式的主类 */
public class CalcMain {
    public static void main(String[] args) throws CalcException
    {
        String code ="";
        if(args.length>0)
            code=args[0];
        try {
            System.out.println("编译表达式:"+code);
            compileRun(code,false);
        } catch (CalcException ce) {
            System.err.println(ce.getMessage());
        }
    }

    public static Object compileRun(String code,boolean checkBytes)  throws CalcException
    {
        CalcCompiler compiler= new CalcCompiler();
        compiler.compile(code);
        ClassWriter classWriter =compiler.generator.classWriter;
        ClassRunArg runArg = new ClassRunArg();
        runArg.classPaths = new String[]{};
        runArg.checkBytes = checkBytes;
        runArg.bytes = classWriter.toByteArray();
        runArg.className =  compiler.generator.genFullName.replace("/",".");
        runArg.method = compiler.generator.executeMethodName ;
        runArg.argTypes = new Class<?>[]{};
        runArg.args = new Object[]{};

        ClassRuner classRuner = new ClassRuner();
        try {

            Object result= classRuner.runBytes(runArg);
            System.out.println(result);
            return result;
        }
        catch (Exception e)
        {
            System.out.println("调用异常:"+e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
