//MainSample.j

package samples;
import java.lang.String;
import java.lang.System;
import java.lang. StringBuilder;
import java.lang.Integer;

void test_string_contact()
{
 System.out.println( "Hello_"+getSize()+"_1");
}

  void testCompare() {
     int a = 10;
     int b = 20;
     System.out.println("a == b = " + (a == b) );
     System.out.println("a != b = " + (a != b) );
     System.out.println("a > b = " + (a > b) );
     System.out.println("a < b = " + (a < b) );
     System.out.println("b >= a = " + (b >= a) );
     System.out.println("b <= a = " + (b <= a) );
  }

void testLogic()
{
    boolean a=true;
    boolean b= false;
     System.out.println(" &&  = " + (getNot(a)&& getNot(b)));
         System.out.println(" ||  = " + (getNot(a)||getNot(b)) );
         System.out.println("! " + !(getNot(a) ));
}

void main()
 {
 testCompare( );
testLogic();
     System.out.println( "Hello");
    test_string_contact();
     testPrimitive();

       int a=100;
       int b=25;
        int c = 4;
        int d = 10;
        int e=2;
       // int a=10;
    System.out.println( a+b*c- d/e);

    while(c>0)
    {
         System.out.println( "c="+c);
         c=c-1;
    }
          int size = getSize();
          testStringBuilder( );
  }

int getSize()
{
         return 10;
}

boolean getNot(boolean a)
{
    return !a;
}

void testStringBuilder(){
        StringBuilder sb = new StringBuilder(10);
        sb.append("Test ..");
        System.out.println(sb);
        sb.append("!");
        System.out.println(sb);
        sb.insert(8, "Java");
        System.out.println(sb);
        sb.delete(5,8);
        System.out.println(sb);
    }

      void testPrimitive() {
        boolean b1 = true;
        System.out.println("Bool :" + b1);
         boolean b2 = false;
          System.out.println("Bool :" + b2);
        int  i1=100;
        System.out.println("Integer :" + i1);
        String s1 ="String";
        System.out.println("String    :" + s1);

                System.out.println("基本类型：int 二进制位数：" + Integer.SIZE);
                System.out.println("包装类：java.lang.Integer");
                System.out.println("最小值：Integer.MIN_VALUE=" + Integer.MIN_VALUE);
                System.out.println("最大值：Integer.MAX_VALUE=" + Integer.MAX_VALUE);
    }
