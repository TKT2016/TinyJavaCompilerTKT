package testjava;

public class TestArray {
    void test1()
    {
        int[] arr = new int[10];
        arr[0] = 56;
        System.out.println( arr.length);
        System.out.println( arr[0]);
       // Integer.MIN_VALUE =0;
    }

    void test2(String[] args)
    {
        // int size = 10;
        int size = 10;
        // 定义数组
        int[] arr = new int[size];
        arr[0] = 56;
        // 计算所有元素的总和
        int total = 0;
        for(int x = 0; x < arr.length; x = x+1)
        {
            int var1= getInt();
            total=total+arr[x]+var1;
        }
        System.out.println( arr[0]);
        System.out.println("总和为： " + total);
    }

    int getInt()
    {
        return  2;
    }
}
