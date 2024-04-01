package jtinyc.emits;

import jtinyc.lex.TokenKind;
import jtinyc.symbols.*;
import jtinyc.trees.*;
import jtinyc.utils.CompileError;
import org.objectweb.asm.*;
import java.io.File;
import java.io.FileOutputStream;
import static org.objectweb.asm.Opcodes.*;

/** 生成辅助类 */
public class EmitUtil {
    /** 设置asm计算栈帧等，如果异常提示并继续 */
    public static void visitMaxs(MethodVisitor mv, final int maxStack, final int maxLocals) {
        try {
            mv.visitMaxs(maxStack, maxLocals);
        } catch (Exception ex) {
            System.err.println("MethodVisitor.visitMaxs exception:" + ex.getMessage());
        }
    }

    /** 自动计算asm计算栈帧等，如果异常提示并继续 */
    public static void visitMaxs(MethodVisitor mv) {
        visitMaxs(mv,0, 0);
    }

    /** 创建一个public的函数写入器  */
    public static MethodVisitor emitMethodDeclare( DMethodSymbol methodSymbol,ClassWriter classWriter)
    {
        String methodName = methodSymbol.name;//方法名称
        String desc = SymbolSignatureUtil.getParamsSignature(methodSymbol,true);//方法描述
        MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC, methodName, desc, null, null);
        return methodVisitor;
    }

    /** 创建public的,继承Object的,自动计算栈帧的 ClassWriter, */
    public static ClassWriter newClassWriter(String signName ) {
        int flag = ClassWriter.COMPUTE_FRAMES|ClassWriter.COMPUTE_MAXS ;
        ClassWriter classWriter = new ClassWriter(flag );
        String genClassName = SymbolSignatureUtil.nameToSign(signName);
        String superClass = "java/lang/Object";//父类为Object
        String[] interfacesSigns = new String[]{};//不继承任何接口
        int access = Opcodes.ACC_PUBLIC +Opcodes.ACC_SUPER;
        classWriter.visit(Opcodes.V1_8, access, genClassName, null, superClass , interfacesSigns);
        return classWriter;
    }

    /** 生成一个无参默认构造函数 */
    public static void emitDefaultConstuctor(ClassWriter classWriter)
    {
        MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC,"<init>", "()V", null, null);
        mv.visitCode();//开始
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        mv.visitInsn(RETURN);
        EmitUtil.visitMaxs(mv); //计算栈帧
        mv.visitEnd();//结束
    }

    /** 保存字节码文件,返回生成的class文件路径 */
    public static String saveClassByteFile(ClassWriter classWriter, String saveFilePath, String packageName, String className)
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
            return file.getAbsolutePath();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /** 根据函数返回值生成不同的返回指令 */
    public static void emitReturn(JCMethod tree, MethodVisitor methodWriter) {
        DMethodSymbol meth =tree.methodSymbol;
        BTypeSymbol retType = meth.returnType;
        emitRetOpCode(methodWriter, retType);
    }

    public static void emitRetOpCode(MethodVisitor mv, BTypeSymbol retype)
    {
        if(SymbolUtil.isVoid(retype))
        {
            mv.visitInsn(Opcodes.RETURN);
        }
        else if(retype instanceof RClassSymbol)
        {
            RClassSymbol rClassSymbol = (RClassSymbol)retype;
            Class<?> type  = rClassSymbol.clazz;
            int op= OpCodeSelecter.ret(type);
            mv.visitInsn(op);
        }
        else {
            mv.visitInsn(ARETURN);
        }
    }

    public  static int getBinaryOpCode(JCBinary tree)
    {
        BTypeSymbol resultType = tree.symbol.getTypeSymbol();
       // RClassSymbol rClassSymbol = (RClassSymbol) resultType;
        TokenKind opcode = tree.opcode;
        if (opcode.equals(TokenKind.ADD))
           return Opcodes.IADD;//OpCodeSelecter.add(rClassSymbol.clazz);
        else if (opcode.equals(TokenKind.SUB))
            return Opcodes.ISUB;//OpCodeSelecter.sub(rClassSymbol.clazz);
        else if (opcode.equals(TokenKind.MUL))
            return  Opcodes.IMUL;//OpCodeSelecter.mul(rClassSymbol.clazz);
        else if (opcode.equals(TokenKind.DIV))
            return Opcodes.IDIV;//OpCodeSelecter.div(rClassSymbol.clazz);
        else
            throw new CompileError();
    }


    public static void emitAND( JCBinary tree, EmitContext arg ,FileEmit gen)
    {
        MethodVisitor mv =arg.mv;
        Label l1 = new Label();
        Label l2 = new Label();

        gen.emit(tree.left,arg);
        mv.visitJumpInsn(IFEQ, l1);
        gen.emit(tree.right,arg);
        mv.visitJumpInsn(IFEQ, l1);
        mv.visitInsn(ICONST_1);
        mv.visitJumpInsn(GOTO, l2);
        mv.visitLabel(l1);
        mv.visitInsn(ICONST_0);
        mv.visitLabel(l2);
    }

    public static void emitOR( JCBinary tree, EmitContext arg ,FileEmit gen)
    {
        MethodVisitor mv =arg.mv;
        Label l1 = new Label();
        Label l2 = new Label();
        Label l3 = new Label();

        gen.emit(tree.left,arg);
        mv.visitJumpInsn(IFNE, l1);
        gen. emit(tree.right,arg);
        mv.visitJumpInsn(IFEQ, l2);
        mv.visitLabel(l1);
        mv.visitInsn(ICONST_1);
        mv.visitJumpInsn(GOTO, l3);
        mv.visitLabel(l2);
        mv.visitInsn(ICONST_0);
        mv.visitLabel(l3);
    }

    /*public static void emitInsnGetBoolean( MethodVisitor mv,int insn,boolean firstTrue)
    {
        Label l1 = new Label();
        Label l2 = new Label();

        int b1 = firstTrue?ICONST_1:ICONST_0;
        int b2 =  !firstTrue?ICONST_0:ICONST_1;

        mv.visitJumpInsn(insn , l1);
        mv.visitInsn(b1);
        mv.visitJumpInsn(GOTO, l2);
        mv.visitLabel(l1);
        mv.visitInsn(b2);
        mv.visitLabel(l2);
    }*/

    public static void emitInsnGetFalseTrue(MethodVisitor mv, int insn )
    {
        Label l1 = new Label();
        Label l2 = new Label();

        mv.visitJumpInsn(insn , l1);
        mv.visitInsn(ICONST_0);
        mv.visitJumpInsn(GOTO, l2);
        mv.visitLabel(l1);
        mv.visitInsn(ICONST_1);
        mv.visitLabel(l2);
    }

    public static void emitBooleanEQ(JCBinary tree, EmitContext arg ,FileEmit gen )
    {
        gen.emit(tree.left,arg);
        gen.emit(tree.right,arg);
        emitInsnGetFalseTrue( arg.mv ,Opcodes.IF_ICMPEQ);
    }

    public static void emitBooleanNE(JCBinary tree, EmitContext arg ,FileEmit gen ) {
        gen.emit(tree.left, arg);
        gen.emit(tree.right, arg);
        emitInsnGetFalseTrue( arg.mv, IF_ICMPNE);
    }

    public static void emitIntEQ(JCBinary tree, EmitContext arg ,FileEmit gen)
    {
        gen.emit(tree.left,arg);
        gen.emit(tree.right,arg);
        emitInsnGetFalseTrue( arg.mv ,Opcodes.IF_ICMPEQ);
    }

    public static void emitIntNE(JCBinary tree, EmitContext arg ,FileEmit gen)
    {
        gen.emit(tree.left,arg);
        gen.emit(tree.right,arg);
        emitInsnGetFalseTrue( arg.mv ,Opcodes.IF_ICMPNE);
    }

    public static void emitCompare(JCBinary tree, EmitContext arg ,FileEmit gen,int opcode)
    {
        gen.emit(tree.left,arg);
        gen.emit(tree.right,arg);
        emitInsnGetFalseTrue(arg.mv,opcode);
    }

    public static void emitStoreField(EmitContext arg , BVarSymbol varSymbol )
    {
        MethodVisitor mv =arg.mv;
        BTypeSymbol typeSymbol = varSymbol.getTypeSymbol();
        int op = varSymbol.isStatic ? PUTSTATIC : PUTFIELD;
        String ownerSign =  SymbolSignatureUtil.getParamsSignature(varSymbol.ownerType,false);
        String descSign =  SymbolSignatureUtil.getParamsSignature( typeSymbol,true);
        mv.visitFieldInsn(op,ownerSign, varSymbol.name,descSign);
    }

    public static void loadConstInteger(MethodVisitor methodVisitor, int ivalue)
    {
        if(ivalue>=0 && ivalue<=5)
        {
            int opcode = OpCodeSelecter.pushIntConst(ivalue);
            methodVisitor.visitInsn(opcode);
        }
        else if(ivalue>=-128 && ivalue<127)
        {
            methodVisitor.visitIntInsn(BIPUSH,ivalue);
        }
        else if(ivalue>=-32768 && ivalue<32767)
        {
            methodVisitor.visitIntInsn(SIPUSH,ivalue);
        }
        else
        {
            methodVisitor.visitLdcInsn(ivalue);
        }
    }
}
