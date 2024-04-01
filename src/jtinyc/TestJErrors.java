package jtinyc;

import tools.FileUtil;

import java.io.File;

public class TestJErrors {
    static  final   String ext =".j";

    public static void main(String[] args)
    {
        testone("Terrors\\ErrorTest");
       // testone("Terrors\\Err1Lex");
    //    testone("Terrors\\Err2Parse");
       // testone("Terrors\\ErrNoPackage");
       // testone("Terrors\\Err3Parse");
       // testone("Terrors\\Err4Parse");
        //testone("Terrors\\Err5Analyz");
        //testone("Terrors\\Err6Analyz");
        //testone("Terrors\\Err7Analyz");
       // testone("Terrors\\Err8Analyz");
       // testone("Terrors\\Err9Return");
    }

    static void compileRun( File item,int i)
    {
        String src = item.getAbsolutePath();
        if (src.endsWith(ext)) {
            System.out.println("编译运行第"+i+":"+ src);
            Main.compileRun(src,false);
        }
    }
    /*void F6( , int a)
    {
    }*/


    static void testone(String srcName)
    {
        String baseDir = FileUtil.getCurrentPath()+"\\samples\\";
        //String outPath =baseDir+"out\\";
        String feoSrc=srcName;
        //String ext =".j";
       // feoSrc = "test";
      //  feoSrc = "HelloWorld";
       /* feoSrc = "PrimitiveTypeTest";
        feoSrc = "Dog";
        feoSrc = "Puppy";
        feoSrc = "Employee";
        feoSrc ="PrimitiveTypeShow";
        feoSrc ="TestLocalVar";
        feoSrc ="TestOp";
        feoSrc ="TestLogicOp";
        feoSrc ="TestCompare";
        feoSrc ="TestWhile";
        feoSrc ="TestFor";
        feoSrc ="TestBreak";
        feoSrc ="TestContinue";
        feoSrc ="TestIf";
        feoSrc ="TestInteger";
        feoSrc ="TestMath";
        feoSrc ="TestString";
        feoSrc ="TestStringBuilder";
        feoSrc ="TestArray";
         feoSrc ="TestStackFrame";
        feoSrc ="TestOptimizer";*/

        feoSrc+=ext;
        System.out.println(baseDir+feoSrc);
        Main.compileRun(baseDir+feoSrc,false);
    }
}
