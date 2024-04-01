package testjava;

public class TestJavaASMEmit {
    int field1;
    String field2;
    void testMethodDecl( )
    {
        System.out.println("STRING");
    }

    void testFieldAccess1(int p1)
    {
        field1 = p1;
        System.out.println(field1);
    }

    void testFieldAccess2(String p1)
    {
        field2 = p1;
        System.out.println(field2);
    }

   static  void testStaticMethodDecl()
    {

    }
}
