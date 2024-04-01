//TestIf.j
package samples;
import java.lang.String;
import java.lang.System;

    void main(String[] args){
      int x = 30;
      int y = 10;

     if( x < 20 ){
         System.out.println("这是 if 语句");
      }else{
         System.out.println("这是 else 语句");
      }

      if( x == 30 ){
         if( y == 10 ){
             System.out.println("X = 30 and Y = 10");
          }
       }

       if( x == 10 ){
                System.out.println("Value of X is 10");
             }else if( x == 20 ){
                System.out.println("Value of X is 20");
             }else if( x == 30 ){
                System.out.println("Value of X is 30");
             }else{
                System.out.println("这是 else 语句");
             }
    }


