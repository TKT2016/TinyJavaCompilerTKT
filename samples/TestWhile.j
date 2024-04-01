//TestWhile.j
package samples;
import java.lang.String;
import java.lang.System;
import java.lang.Object;

void main() {
      int x = 10;
      while( x < 20 ) {
         System.out.print("value of x : " + x );
         x=x+1;
         System.out.println();
      }
      println("test println");
   }

void println(  Object obj)
{
      System.out.println(obj);
}