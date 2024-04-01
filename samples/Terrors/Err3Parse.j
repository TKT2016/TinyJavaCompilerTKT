/*
Err3Parse.j
语句语法错误
*/
package samples.Terrors;
import java.lang.String;
import java.lang.System;

void main(String[] args) {

      //非法的表达式语句成分
        +
    //语句块
    {
    }

   // ELSE缺少IF
     else
           System.out.println("ELSE");
    //if语句
    if();
    if(){};
         if{ System.out.println("0")}
    if{}
     System.out.println("0");

//测试while
    while;
    while();
    while{}
//测试return
    return
//测试for
for;
 for(;;);
 for(return 1; ;)
 {

}
 for(int i=0;i<10;i=i+1)
 {
 }


//右大括号没有匹配的左大括号
}