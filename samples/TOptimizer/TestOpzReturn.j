/*
 * Jtiny compiler source code
 * License : the GNU General Public License
 * Copyright (C) 2021-2024 Chines JSSQSY WANG
 */
//TestOpzReturn.j
/*
* 测试return后死代码消除
*/

package samples.TOptimizer;
import java.lang.String;
import java.lang.System;

    void main()
	{
        testBlock();
        testIf(10);
        testIf(-1);
        testWhile(1);
        //  testFor( 5);
    }

    void testBlock()
    {
            {
             System.out.println("BLOCK befor return ");
                return;
                 System.out.println("BLOCK after return ");
                 }
    }

void testIf(int x)
{
    if(x>0)
    {
              System.out.println("IF x>0 befor return ");
              return;
                System.out.println("IF after return ");
    }
    else
    {
               System.out.println("IF x<=0 befor return ");
               return;
               System.out.println("IF after return ");
    }
}

void testWhile(int x)
{
    while(x>0)
    {
           System.out.println("WHILE x>0 befor return ");
           return;
          System.out.println("WHILE after return ");
           x=x-1;
    }
}
/*
void testFor(int x)
{
    for(int i=0;i<x;i=i+1)
    {
           System.out.println("FOR befor return ");
           return;
           System.out.println("FOR after return ");
    }
}*/