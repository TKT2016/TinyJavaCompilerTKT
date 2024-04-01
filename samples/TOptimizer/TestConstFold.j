//TestConstFold.j
/*
* 测试常量折叠优化
*/

package samples.TOptimizer;
import java.lang.String;
import java.lang.System;

    void main()
	{
	   testUnary1( );
        testUnary2(10,false);

        testBinary1();
         testBinary2(10,false);
    }

// 一元运算表达式 优化
  void testUnary1()
  {
     int a=+(1+2);
     System.out.println("a = " + a );
     int b = -(3+4);
     System.out.println("b = " + b );
     boolean c= !(true&&false);
     System.out.println("c = " + c );
  }

// 一元运算表达式 不优化
  void testUnary2(int p1,boolean p2)
  {
     int  a=+(p1);
     System.out.println("a = " + a );
     int b = -(p1+p1);
     System.out.println("b = " + b );
     boolean c= !(p2);
     System.out.println("c = " + c );
  }

// 二元运算表达式 优化
void testBinary1()
{
         int a = 10+2+100;
         System.out.println("a = " + a );

        a= 3-8;
             System.out.println("a = " + a );
             a= 3*8;
             System.out.println("a = " + a );
             a= 16/8;
             System.out.println("a = " + a );

             a= 1+2-3*4/5+ 16/8;
             System.out.println("1+2-3*4/5+ 16/8 = " + a );

     boolean b =false;
     b= true&&false;
     System.out.println("b = " + b );
     b= true|| false;
     System.out.println("b = " + b );
     b= !true;
     System.out.println("b = " + b );
     b= 5>6;
	 System.out.println("b = " + b );
	 b= 5<6;
	 System.out.println("b = " + b );
	 b= 5==6;
	 System.out.println("b = " + b );
	 b= 5>=6;
	 System.out.println("b = " + b );
     b= 5>=6;
	 System.out.println("b = " + b );
	 b= 5!=6;
	 System.out.println("b = " + b );

	 String s="";
	 s="STR"+100;
	  System.out.println("s = " + s );
	   s="A"+"B";
	  System.out.println("s = " + s );

	   s=100+"STR";
      	 System.out.println("s = " + s );
       s=1+2+"STR"+4;
        System.out.println("s = " + s );
}


// 二元运算表达式 不优化
void testBinary2( int p1,boolean p2)
{
         int a = p1+10+2+100;
         System.out.println("a = " + a );

        a= p1-3-8;
             System.out.println("a = " + a );
             a=p1- 3*8;
             System.out.println("a = " + a );
             a= p1/8;
             System.out.println("a = " + a );

             a= p1+1+2-3*4/5+ 16/8;
             System.out.println("p1 + 1+2-3*4/5+ 16/8 = " + a );

             boolean b =false;
             b= p2&&false;
             System.out.println("b = " + b );
             b= p2|| false;
             System.out.println("b = " + b );
             b= !p2;
             System.out.println("b = " + b );
             b= p1>6;
             System.out.println("b = " + b );
             b= p1<6;
             System.out.println("b = " + b );
             b= p1==6;
             System.out.println("b = " + b );
             b= p1>=6;
             System.out.println("b = " + b );
             b= p1>=6;
             System.out.println("b = " + b );
             b= p1!=6;
             System.out.println("b = " + b );

             String s="";
             s="STR"+p1;
              System.out.println("s = " + s );
               s=p2 +"B";
              System.out.println("s = " + s );
               s=p1+"STR";
                 System.out.println("s = " + s );
               s=p2+"STR"+4;
                System.out.println("s = " + s );
}
