//TestAssign.j
/*
* 测试赋值优化
*/

package samples.TOptimizer;
import java.lang.String;
import java.lang.System;

void main()
{
	   test1( "ARG_");
       test2("A2_");
}

// 赋值 优化
  void test1(String p1)
  {
        String a="A";
    	a=p1+"B1";
    	a=p1+"B2";
    	a=p1+"B3";
    	a=p1+"B4";
    	System.out.println("a = " + a );

       a=p1+"D1";
       //String b=p1+"STR1";
        a=p1+"D2";
       System.out.println("a = " + a );
        a=p1+"E";
       System.out.println("a = " + a );
/*
    	a="D";
        String b="E";
    	a="F";
    	b="G";
    	a="H";
    	 System.out.println("a = " + a );
    	  System.out.println("b = " + b );*/
  }

  void test2(String p1)
  {
        String a=p1+"A";
    	a=p1+"B";
		if(1==2){//if (a.equals("B")) {
			System.out.println("1==2");
		}
    	a=p1+"C";
    	System.out.println("a = " + a );
    	a=p1+"D";
        String b=p1+"E";
    	a=p1+"F";
    	while(1>2){
    	}
    	b=p1+"G";
    	a=p1+"H";
    	 System.out.println("a = " + a );
    	  System.out.println("b = " + b );
  }
