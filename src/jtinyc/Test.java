/*
 * Jtiny compiler source code
 * License : the GNU General Public License
 * Copyright (C) 2021-2024 Chines JSSQSY WANG
 */
package jtinyc;

import tools.FileUtil;

import java.io.File;

public class Test {
    static  final   String ext =".j";

    public static void main(String[] args)
    {
       // testone( "TestWhile");
       // testBat(-1);
      // testone("MainSample");
       // testone("PrimitiveTypeTest");
        //testone("TestLogicOp");
       // testone("TestString");
       // testone("TestStringBuilder");
       // testone("HelloWorld");
        testone("TestOp");
       //    testone("TestFor");
      // testone("TOptimizer/TestOpzReturn");
        //testone("TOptimizer/TestConstFold");
     //  testone("TOptimizer/TestAssign");
      //  testone("TOptimizer/TestConstSpread");
      //  testone("TOptimizer/TestDeadCode");
    }

    static void testBat(int index)
    {
       // String classPath0 = "";
        String baseDir = FileUtil.getCurrentPath()+"\\samples\\";
        File file = new File(baseDir);

        File[] tempList = file.listFiles();
        System.out.println("共有文件:"+tempList.length);
        if(index!=-1)
        {
            compileRun(tempList[index],index);
            return;
        }

        for (int i = 0; i < tempList.length; i++) {
            File item = tempList[i];
            compileRun(item,i);
        }
    }

    static void compileRun( File item,int i)
    {
        String src = item.getAbsolutePath();
        if (src.endsWith(ext)) {
            System.out.println("编译运行第"+i+":"+ src);
            Main.compileRun(src,false);
        }
    }

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
