package testjava;

import java.io.*;
class TinyTestJava {
    int a;

    void main2(String[] args) {
        for(int x = 10; x < 20; x = x+1) {
            System.out.print("value of x : " + x );
            System.out.println();
        }

        System.out.println();
        for(int x = 10; x < 20; x = x+1) {
            System.out.print("value of x : " + x );
            System.out.println();
        }

        for(int x = 10; x < 20; x = x+1,a++) {
            System.out.print("value of x : " + x );
            System.out.println();
            int a=0;
            System.out.print("value of a : " + a );
        }
    }

    void test_string_contact()
    {
        System.out.println( "Hello_"+getSize()+"_1");
    }

    int getSize()
    {
        return 10;
    }

    void fa1()
    {
        this.set3(50);
    }

    Object set3(int f3)
    {
        // int a=1;
        int b = 100;
        f3=100;
        return f3;
    }

    void iset(String[] args){
        Integer x = 5;
        x =  x + 10;
        System.out.println(x);
    }

    public void iset2() {
        Integer x = 5;
        x = 15;
        System.out.println(x);
        System.out.println((Integer)15);
    }

    int f3(boolean b)
    {
        if(b)
            return 1;
        return -1;
    }
}
