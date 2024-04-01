
package testjava;
import java.lang.String;
import java.lang.System;
public class TestAssign {

	void main(String[] args) {
		test1();
	}

	// 赋值 优化
	void test1() {
		System.out.println(System.out);
		String a = "A";
		a = "B";
		a = "C";
		System.out.println("a = " + a);
		a = "D";
		String b = "E";
		a = "F";
		b = "G";
		a = "H";
		System.out.println("a = " + a);
		System.out.println("b = " + b);
	}

	void test2() {
		String a = "A";
		a = "B";
		if(1==2){//if (a.equals("B")) {
			System.out.println("1==2");
		}
		a = "C";
		System.out.println("a = " + a);
		a = "D";
		String b = "E";
		a = "F";
		b = "G";
		a = "H";
		System.out.println("a = " + a);
		System.out.println("b = " + b);
	}
}