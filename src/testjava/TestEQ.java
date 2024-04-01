package testjava;

public class TestEQ {
    private static boolean test1(int a,int b)
    {
        return a==b;
    }
    private static boolean test1N(int a,int b)
    {
        return a!=b;
    }

    private static boolean test2(TestEQ a,Object b)
    {
        return a==b;
    }

    private static boolean test3(boolean a,boolean b)
    {
        return a==b;
    }
    private static boolean test3N(boolean a,boolean b)
    {
        return a!=b;
    }

    private static boolean test4(String a,String b)
    {
        return a==b;
    }

    private static void vo1( )
    {

    }

    private static void vo2( )
    {

    }
    //private static boolean test6( )
    //{
        //return vo1()==vo2();
   // }
   /* private static boolean test5(int a,boolean b)
    {
        return a==b;
    }*/
}
