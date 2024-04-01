//TestConstSpread.j
/*
* 测试常量传播优化
*/

package samples.TOptimizer;
import java.lang.String;
import java.lang.System;
import java.lang.Boolean;

    void main()
	{
	   test1( );
        test2( );
        test3();
    }

  void test1()
  {
      String a ="A";
      String b="B";
      String c=a+b;
     System.out.println("c = " + c );
  }

    void test2()
    {
        boolean a = true;
        if(a)
            System.out.println("a IF THEN " );
        else
             System.out.println("a IF ELSE" );
        boolean b=false;

        boolean c=a&&b;
         if(c)
                    System.out.println("cIF THEN " );
       else
                     System.out.println("c IF ELSE" );
        boolean d=!a;

       System.out.println("c = " + c );
       System.out.println("d = " + d );
    }

void test3()
{
    int a=3;
    for(int i=a;i<10;i=i+1)
    {
     System.out.println("i = " + i);
    }
}
