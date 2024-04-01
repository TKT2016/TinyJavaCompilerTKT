// TestDeadCode.j
/*
* 测试死代码优化
*/

package samples.TOptimizer;
import java.lang.String;
import java.lang.System;

 void main()
{
	 test1( true);
}

  void test1(boolean p1)
  {
         if(false)
             System.out.println("IF "  );

        if(true)
             System.out.println("IF THEN  "  );
        else
              System.out.println("IF ELSE "  );

         while(false)
        {
            System.out.println("WHILE" );
        }

       if(p1)
            System.out.println("P1 IF " );

       if(p1)
               System.out.println("P1 IF THEN  "  );
        else
               System.out.println("P1 IF ELSE "  );
  }

