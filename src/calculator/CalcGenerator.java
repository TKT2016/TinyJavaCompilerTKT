package calculator;

import calculator.ast.*;
import org.objectweb.asm.*;

import java.io.File;
import java.io.FileOutputStream;

import static org.objectweb.asm.Opcodes.*;

/* 字节码生成器 */
public class CalcGenerator {
    public final String superSign = "java/lang/Object"; //超类签名,默认继承Object
    public final String genPackageName="calculators";//生成的包名称
    public final String genClassName="TestExpr";//生成的类名称
    public final String genFullName = genPackageName+"/"+genClassName; //生成的类的全名称
    public final String executeMethodName = "execute"; //生成的默认方法名称
    public final String executeMethodSign = "()D"; //生成的execute方法签名

    public final String saveDir = "out/ASMEmits"; // class文件保存路径

    ClassWriter classWriter; //asm写class字节码的全局类
    MethodVisitor executeMethodWriter; //execute方法体内java指令生成

    void emitClass(Expr expr)
    {
        /* flag用于设置指定asm自动计算程序的栈帧、最大栈高度，最多局部变量*/
        int flag = ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS;
        classWriter = new ClassWriter(flag);
        classWriter.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC,genFullName , null, superSign, new String[]{});
        /* 生成默认的无参构造函数 */
        emitEmptyConstructor(classWriter,superSign);
        emitExecuteMethod(expr); //生成execute方法
        classWriter.visitEnd();//类结束
        save(classWriter,saveDir, genPackageName ,genClassName ); //保存class文件
    }

    void emitExecuteMethod(Expr expr)
    {
        /* 开始生成 execute 方法 */
        executeMethodWriter = classWriter.visitMethod(Opcodes.ACC_PUBLIC, executeMethodName, executeMethodSign, null, null);
        executeMethodWriter.visitCode(); //开始生成函数体

        emitExpr(expr);//生成表达式的指令
        executeMethodWriter.visitInsn(DRETURN); //生成返回double类型指令

        executeMethodWriter.visitMaxs(0, 0);//调用此方法触发计算
        executeMethodWriter.visitEnd(); //方法结束
    }

    private void emitExpr(Expr expr)
    {
        if(expr instanceof DoubleValueExpr)
        {
            emitDouble((DoubleValueExpr)expr);
        }
        else if(expr instanceof BracketsExpr)
        {
            emitBrackets((BracketsExpr)expr);
        }
        else if(expr instanceof BinaryExpr)
        {
            emitBinary((BinaryExpr)expr);
        }
    }

    private void emitDouble(DoubleValueExpr expr)
    {
        Double value = Double.parseDouble(expr.doubleText);
        executeMethodWriter.visitLdcInsn(value);
    }

    private void emitBrackets(BracketsExpr bracketsExpr)
    {
        emitExpr(bracketsExpr.innerExpr);
    }

    private void emitBinary(BinaryExpr binaryExpr)
    {
        emitExpr(binaryExpr.left);
        emitExpr(binaryExpr.right);
        switch (binaryExpr.opToken.kind)
        {
            case ADD:
                executeMethodWriter.visitInsn(DADD);
                break;
            case SUB:
                executeMethodWriter.visitInsn(DSUB);
                break;
            case MUL:
                executeMethodWriter.visitInsn(DMUL);
                break;
            case DIV:
                executeMethodWriter.visitInsn(DDIV);
                break;
        }
    }

    public static void emitEmptyConstructor(ClassWriter classWriter,String superClassSign)
    {
        MethodVisitor mv= classWriter.visitMethod(Opcodes.ACC_PUBLIC,"<init>","()V", null,null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, superClassSign, "<init>", "()V",false);
        mv.visitInsn(RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }

    public static boolean save(ClassWriter classWriter, String saveFilePath,String packageName,String className)
    {
        byte[] data = classWriter.toByteArray();
        String packagePath = packageName.replace(".","/");
        String folderPath =  saveFilePath+"/"+packagePath+"/";

        File folder = new File(folderPath);
        if (!folder.exists() && !folder.isDirectory()) {
            folder.mkdirs();
        }
        String fullpath =folderPath+ className+".class";
        File file = new File(fullpath);
        try {
            FileOutputStream fout = new FileOutputStream(file);
            fout.write(data);
            fout.close();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }
}
