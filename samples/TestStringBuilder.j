//TestStringBuilder.j
package samples;
import java.lang.String;
import java.lang.System;
import java.lang.StringBuilder;

      void main( ){
        StringBuilder sb = new StringBuilder(10);
        sb.append("Test StringBuilder");
        System.out.println(sb);
        sb.append("-");
        System.out.println(sb);
        sb.insert(8, "Java");
        System.out.println(sb);
        sb.delete(5,8);
        System.out.println(sb);
    }

