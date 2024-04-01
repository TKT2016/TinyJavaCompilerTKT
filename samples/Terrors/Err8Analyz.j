/*
Err8Analyz
语义错误 成员访问
*/
package samples.Terrors;
import java.lang.String;
import java.lang.System;
import java.lang.Integer;
import java.lang.Exception;

void main(String[] args) {
            // 测试赋值
          Integer.MIN_VALUE ="TEST";
         Integer.MIN_VALUE =0;
        System.out=System.out;
      //System.out.println("hell"+"o");

       // 测试方法调用
          System.out.println( System);
       System.out.println( System.out);
       System.out.println( System.out.println);
       System.out.println( System.out.println(100));
      System.out.println( 1,2);

    //测试new表达式
    Exception ex1 = new Exception();
    Exception ex2 = new Exception(1000);

    // 测试 new 数组
    boolean[] arr1= new boolean[6];
    //  boolean[] arr2= new boolean[ ];
    // boolean[] arr2= new boolean[2,4];
    boolean[] arr3= new boolean[true];
    ErrorType1[] et1 =  new ErrorType1[8];

    //测试访问数组表达式
     arr1 [0] =false;
      arr1 [1] =33;
      ex1 [0] =false;
       ex1 ["T"] =false;

     Err1[] errs = new Err1[100];
}


/*
//测试赋值
void f1()
{
    int a=1;
    a=(123);
    A b=100;
    String a="STR";
}
*/
/*
//一元运算表达式
void f2(int p1,boolean p2)
{
   p1=-1;
   p1=+1;
   p1=!1;
  // p1=*1;
   p2=-1;
   p2=!1;
   p2=+1;
   //p2=/1;
}
*/
/*
// 二元运算表达式 1
void testBin1(int a ,int b,int c)
{
      a=b+c;
       a=b-c;
       a=b*c;
       a=b/c;
       a=b&&c;
       a=b||c;
      a=b>c;
      a=b>=c;
      a=b<c;
      a=b>=c;
      a=b==c;
      a=b!=c;
}
*/
/*
// 二元运算表达式 2
void testBin2(boolean a ,boolean b,boolean c)
{
      a=b+c;
       a=b-c;
       a=b*c;
       a=b/c;
       a=b&&c;
       a=b||c;
      a=b>c;
      a=b>=c;
      a=b<c;
      a=b>=c;
      a=b==c;
      a=b!=c;
}
*/
/*
// 二元运算表达式 3
void testBin3(String a ,String b,String c)
{
      a=b+c;
       a=b-c;
       a=b*c;
       a=b/c;
       a=b&&c;
       a=b||c;
      a=b>c;
      a=b>=c;
      a=b<c;
      a=b>=c;
      a=b==c;
      a=b!=c;
}
*/
/*
// 二元运算表达式 4
void testBin4(String a ,String b,int c)
{
      a=b+c;
       a=b-c;
       a=b*c;
       a=b/c;
       a=b&&c;
       a=b||c;
      a=b>c;
      a=b>=c;
      a=b<c;
      a=b>=c;
      a=b==c;
      a=b!=c;
}
*/
/*
// 二元运算表达式 5
void testBin5(String a ,int b,int c)
{
      a=b+c;
       a=b-c;
       a=b*c;
       a=b/c;
       a=b&&c;
       a=b||c;
      a=b>c;
      a=b>=c;
      a=b<c;
      a=b>=c;
      a=b==c;
      a=b!=c;
}
*/
/*
// 二元运算表达式 5
void testBin5(String a ,String b,boolean c)
{
      a=b+c;
       a=b-c;
       a=b*c;
       a=b/c;
       a=b&&c;
       a=b||c;
      a=b>c;
      a=b>=c;
      a=b<c;
      a=b>=c;
      a=b==c;
      a=b!=c;
}*/