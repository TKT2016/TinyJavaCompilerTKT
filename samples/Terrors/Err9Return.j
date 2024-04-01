/*
Err9Return.j
返回错误错误
*/
package samples.Terrors;
import java.lang.String;
import java.lang.System;
import java.lang.Integer;
import java.lang.Exception;

void main(String[] args) {
    System.out.println(f3(false));
}
/*
void f1()
{
}
*/
/*int f2()
{
    return 1;
}
*/

int f3(boolean b)
{
    if(b)
        return 1;
    // return -1;
}

int f4(boolean b)
{
    if(b)
        return 1;
     return -1;
}

int f5(boolean b,boolean b2)
{
    if(b)
        return 1;
     else if(b2)
        return 2;
}

int f6(boolean b,boolean b2)
{
    if(b)
        return 1;
     else if(b2)
        return 2;
    else
        return -1;
}