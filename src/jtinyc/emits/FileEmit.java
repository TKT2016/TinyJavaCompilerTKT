package jtinyc.emits;

import jtinyc.lex.TokenKind;
import jtinyc.symbols.*;
import jtinyc.trees.*;
import jtinyc.utils.*;
import org.objectweb.asm.*;
import tools.FileUtil;
import static org.objectweb.asm.Opcodes.*;

/** 源文件生成器 */
public class FileEmit extends TreeScanner<EmitContext>
{
    /** 生成 */
    public void emit(JCFile fileTree )
    {
        EmitContext arg = new EmitContext();
        visitFile(fileTree,arg);
    }

    /** 文件字节码生成 */
    @Override
    public void visitFile(JCFile fileTree  , EmitContext arg) {
        DSourceFileSymbol sourceFileSymbol  = fileTree.fileSymbol;
        /* 根据源文件名称生成类型签名 */
        String fileClassSignature = SymbolSignatureUtil.getParamsSignature(sourceFileSymbol,false);
        /* 创建classWriter */
        arg.classWriter= EmitUtil.newClassWriter(fileClassSignature);
        /* classWriter记录源文件信息 */
        arg.classWriter.visitSource(fileTree.log.sourceFile, null);
        /* 生成一个无参数的默认构造函数 */
        EmitUtil. emitDefaultConstuctor(arg.classWriter);
        /* 生成所有函数 */
        for(JCMethod method:fileTree.methods)
            method.scan(this,arg);
        /* classWriter结束 */
        arg.classWriter.visitEnd();
        /* 保存class字节码文件到out文件夹 */
        String className = FileUtil.getNameNoExt(fileTree.log.sourceFile);
        sourceFileSymbol.compiledClassFile = EmitUtil. saveClassByteFile( arg.classWriter , "out", sourceFileSymbol.packageName,className) ;
    }

    /** 方法字节码生成 */
    @Override
    public void visitMethodDef(JCMethod tree, EmitContext arg)
    {
        /* 计算方法内参数和局部变量的地址 */
        new LocalVarAdrScanner( ).visit(tree);
        /* 创建函数体作用域的开始结束标签 */
        tree.scope.createLabels();
        /* 创建一个public的函数写入器 */
        MethodVisitor mv = EmitUtil.emitMethodDeclare(tree.methodSymbol,arg.classWriter);

        /* 设置上下文参数的函数字节码写入器、行号写入器、函数结束标签 */
        arg.mv =mv;
        arg.lineNumberEmit = new LineNumberEmit();
        arg.methodEndLabel =  tree.scope.endLabel ;
        /* 函数体生成正式开始 */
        mv.visitCode();
        /* 标记函数体开始标签 */
        mv.visitLabel( tree.scope.startLabel );
        /* 生成函数体 */
        tree.body.scan(this,arg);
        /* 标记函数体结束标签 */
        mv.visitLabel( tree.scope.endLabel );
        /* 生成返回指令,把栈顶的值返回 */
        EmitUtil.emitReturn(tree,mv);
        /*  函数内变量信息生成 */
        new LocalVariableEmit( ).emitVars(tree,mv);
        /* 计算计算函数栈帧 */
        EmitUtil.visitMaxs(mv);
        /* 函数体生成结束 */
        mv.visitEnd();
    }

    /** 代码块生成 */
    @Override
    public void visitBlock(JCBlock tree, EmitContext arg)
    {
        /* 作用域生成开始结束标签 */
        tree.scope.createLabels();
      //  /* 特殊处理for循环作用域的标签 */
       // if(!tree.isForLoopBody)
       //     arg.mv.visitLabel( tree.scope.startLabel);

        arg.mv.visitLabel( tree.scope.startLabel);

        if(tree.statements ==null ||tree.statements.size()==0 )
        {
            arg.mv.visitInsn(NOP);
        }
        else
        {
            for(JCStatement statement:tree.statements)
            {
                arg.lineNumberEmit.emitLineNumber(arg,statement.line);
                statement.scan(this,arg);
            }
        }
        ///* 特殊处理for循环作用域的标签 */
        //if(!tree.isForLoopBody)
         //   arg.mv.visitLabel(tree.scope.endLabel);
        arg.mv.visitLabel(tree.scope.endLabel);
        /* 生成结束标签行号 */
        arg.lineNumberEmit.emitLineNumber(arg,tree.line,tree.scope.endLabel);
    }

    /** 生成While循环语句 */
    @Override
    public void visitWhileLoop(JCWhile tree, EmitContext arg) {
        /*循环开始标签*/
        Label loopStartLabel = new Label();
        /*循环结束标签*/
        Label loopEndLabel = new Label();

        MethodVisitor mv =arg.mv;
        /*标记循环开始标签*/
        mv.visitLabel(loopStartLabel);

        /* 生成循环条件表达式 */
        emit(tree.cond,arg);
        /* 条件结果为false时,跳转到loopEndLabel结束循环 */
        mv.visitJumpInsn(IFEQ, loopEndLabel);

        /*生成循环体*/
        tree.body.scan(this,arg);
        /*无条件跳转到循环开始标签*/
        mv.visitJumpInsn(GOTO, loopStartLabel);
        /*标记循环结束标签*/
        mv.visitLabel(loopEndLabel);
        /* 生成结束标签行号 */
        arg.lineNumberEmit.emitLineNumber(arg,tree.line,loopEndLabel);
    }

    /** 生成for循环语句*/
   // @Override
  //  public void visitForLoop(JCForLoop tree, EmitContext arg) {
        /* 作用域生成开始结束标签 */
      //  tree.scope.createLabels();
        /* for循环开始标签,和作用域开始标签相同 */
    //    Label loopStartLabel =tree.scope.startLabel;
        /** for循环条件标签 */
     //   Label loopCondLabel = new Label();
        /* for循环结束标签,和作用域结束标签相同*/
       // Label loopEndLabel =tree.scope.endLabel;

       // MethodVisitor mv =arg.mv;
        /*标记循环开始标签*/
       // mv.visitLabel(loopStartLabel);
        /* 生成初始化语句*/
      //  emit(tree.init,arg);
        /* 生成循环条件表达式 */
      //  mv.visitLabel(loopCondLabel);
        /* 生成循环条件表达式 */
      //  emit(tree.cond, arg);
        /* 条件结果为false时,跳转到loopEndLabel结束循环 */
       // if(tree.cond!=null)
      //      mv.visitJumpInsn(IFEQ, loopEndLabel);
        /* 生成循环体 */
       // emit(tree.body, arg);
        /** 生成更新语句 */
       // emit(tree.step,arg);
        /*无条件跳转到循环条件表达式*/
       // mv.visitJumpInsn(GOTO, loopCondLabel);
        /*标记循环结束标签*/
       // mv.visitLabel(loopEndLabel);
        /* 生成结束标签行号 */
    //    arg.lineNumberEmit.emitLineNumber(arg,tree.line,loopEndLabel);
  //  }

    /** 生成if语句 */
    @Override
    public void visitIf(JCIf tree, EmitContext arg) {
        MethodVisitor mv =arg.mv;
        if(tree.elsepart==null)
        {
            /* IF结束标签 */
            Label endLabel = new Label();
            /* 生成条件表达式 */
            emit(tree.cond,arg);
            /* IFEQ表示false,跳转到 endLabel */
            mv.visitJumpInsn(IFEQ, endLabel);
            /* 生成true执行语句 */
            emit(tree.thenpart,arg);
            /* 标记IF结束标签 */
            mv.visitLabel(endLabel);
        }
        else
        {
            /* else标签 */
            Label elseLabel = new Label();//else标签
            /* IF结束标签 */
            Label endLabel = new Label();
            /* 生成条件表达式 */
            emit(tree.cond,arg);
            /* IFEQ表示false,跳转到 elseLabel */
            mv.visitJumpInsn(IFEQ, elseLabel);
            /* 生成true执行语句 */
            emit(tree.thenpart,arg);
            /* 无条件跳转到结束标签 */
            mv.visitJumpInsn(GOTO, endLabel);
            /* 标记else标签 */
            mv.visitLabel(elseLabel);//
            /* 生成else语句 */
            emit(tree.elsepart,arg);//
            /* 标记IF结束标签 */
            mv.visitLabel(endLabel);
        }
    }

    /** 生成return语句 */
    @Override
    public void visitReturn(JCReturn tree, EmitContext arg) {
        /* 生成return表达式 */
        emit(tree.expr,arg);
        /* 跳转到方法的结束标签 */
        arg.mv.visitJumpInsn(GOTO, arg.methodEndLabel);
    }

    /** 生成表达式语句 */
    @Override
    public void visitExprStmt(JCExprStatement tree, EmitContext arg) {
        /* 生成语句内的表达式 */
        emit(tree.expr,arg);
        if(tree.expr instanceof JCMethodInvocation) {
            /* 如果表达式有返回值,需要生成pop指令,把栈顶值清除 */
            if (!SymbolUtil.isVoid(tree.expr.symbol.getTypeSymbol())) {
                arg.mv.visitInsn(POP);
            }
        }
    }

    /** 生成常量表达式 */
    @Override
    public void visitLiteral(JCLiteral tree, EmitContext arg) {
        MethodVisitor mv =arg.mv;
        Object value = tree.value;
        if(value instanceof String)
        {
            /* 生成加载常量池中的字符串 */
            mv.visitLdcInsn(value);
        }
        else if(value instanceof Integer)
        {
            /* 生成加载整数指令 */
            int valueInt =  ((Integer) value).intValue();
            EmitUtil.loadConstInteger(mv,valueInt);
        }
        else if(value instanceof Boolean)
        {
            /* 把布尔值转为整数0或1,再加载整数 */
            boolean valueBoolean =  ((Boolean) value).booleanValue();
            int valueInt = valueBoolean?1:0;
            EmitUtil.loadConstInteger(mv,valueInt);
        }
    }

    /** 生成标识符表达式 */
    public void visitIdent(JCIdent tree, EmitContext arg) {
        MethodVisitor mv =arg.mv;
        Symbol symbol = tree.symbol;

        if (symbol instanceof BTypeSymbol) {
            return; // 情况1:是类型名称符号,不做任何操作
        }
       else if (symbol instanceof BMethodSymbol) {
            /* 情况2:是自定义函数符号, 生成aload 0, 意思是装载this到栈顶*/
            mv.visitVarInsn(ALOAD, 0);
        }
        else  if (symbol instanceof DVarSymbol){
            /* 情况2:是自定义变量符号, 取出这个变量的地址和类型对应lload指令生成 */
            DVarSymbol declVarSymbol = (DVarSymbol) symbol;
            int adr = declVarSymbol.adr;
            int op = OpCodeSelecter.load( declVarSymbol.getTypeSymbol());
            mv.visitVarInsn(op, adr);
        }
        else
        {
            throw new CompileError();
        }
    }

    /** 生成点运算表达式访问 */
    @Override
    public void visitFieldAccess(JCFieldAccess tree, EmitContext arg) {
        /* 生成点运算符左边表达式 */
        emit(tree.selected,arg);
        Symbol symbol = tree.symbol;
      //  if(symbol instanceof RArrayLengthVarSymbol)
      //  {
            /* 情况1:是调用数祖字段，生成ARRAYLENGTH */
      //      arg.mv.visitInsn(ARRAYLENGTH);
     //   }
       // else
            if (symbol instanceof BVarSymbol) {
            /* 情况2: 访问对象字段 */
            BVarSymbol varSymbol = (BVarSymbol) symbol;
            /* 获取这个字段名称 */
            String name = varSymbol.name;
            /* 获取这个字段所属于类型的签名 */
            BTypeSymbol ownerType = SymbolUtil.getOwnerType(varSymbol);
            String owner = SymbolSignatureUtil.getParamsSignature(ownerType, false);
            /* 获取这个字段声明类型的签名 */
            String returnSign = SymbolSignatureUtil.getParamsSignature(varSymbol.getTypeSymbol(), true);
            /* 根据这个字段是否是static的选择指令 */
            int op = varSymbol.isStatic ? GETSTATIC : GETFIELD;
            /* 生成访问字段属性 */
            arg.mv.visitFieldInsn(op, owner, name, returnSign);
        }
    }

    /** 生成一元运算表达式 */
    @Override
    public void visitUnary(JCUnary tree, EmitContext arg) {
        MethodVisitor mv =arg.mv;
        /* 生成表达式 */
        emit(tree.expr,arg);
        TokenKind opcode = tree.opcode;
        if(opcode.equals(TokenKind.SUB))
        {
            /* 1: 取负值, ， 根据类型选择NEG类指令生成 */
            RClassSymbol rClassSymbol =(RClassSymbol)tree.symbol.getTypeSymbol();
            int op = OpCodeSelecter.neg(rClassSymbol.clazz);
            mv.visitInsn(op);
        }
        else if(opcode.equals(TokenKind.NOT))
        {
            /* 2 : 逻辑非运算, 栈顶非0则返回0 ，否则返回 1*/
            Label trueLabel = new Label();
            mv.visitJumpInsn(IFNE, trueLabel);//false
            mv.visitInsn(ICONST_1);//生成true
            Label falseLabel = new Label();
            mv.visitJumpInsn(GOTO, falseLabel);
            mv.visitLabel(trueLabel);
            mv.visitInsn(ICONST_0);//生成false
            mv.visitLabel(falseLabel);
        }
        else if(opcode.equals(TokenKind.ADD))
            /* 3: 加运算，不生成指令 */
            return;
        else
            throw new CompileError();
    }

    /** 生成二元运算表达式 */
    @Override
    public void visitBinary(JCBinary tree, EmitContext arg) {
        BTypeSymbol resultType = tree.symbol.getTypeSymbol();
        TokenKind opcode = tree.opcode;
        if(tree.isStringContact)
        {
            /* 字符串联结 */
            StringConcatEmit stringContactEmit = new StringConcatEmit(arg,this);
            stringContactEmit.start();
            stringContactEmit.append(tree.left);
            stringContactEmit.append(tree.right);
            stringContactEmit.end();
        }
        else if(opcode.equals(TokenKind.EQEQ)||opcode.equals(TokenKind.NOTEQ)) {
            BTypeSymbol leftType = tree.left.symbol.getTypeSymbol();
            if (SymbolUtil.isBoolean(leftType)) {
                if (opcode.equals(TokenKind.EQEQ))
                    EmitUtil.emitBooleanEQ(tree, arg, this);
                else if (opcode.equals(TokenKind.NOTEQ))
                    EmitUtil.emitBooleanNE(tree, arg, this);
            } else {
                if (opcode.equals(TokenKind.EQEQ))
                    EmitUtil.emitIntEQ(tree, arg, this);
                else if (opcode.equals(TokenKind.NOTEQ))
                    EmitUtil.emitIntNE(tree, arg, this);
                }
        }
        else if(SymbolUtil.isBoolean(resultType))
        {
            /* 逻辑运算或比较运算 */
            if(opcode.equals(TokenKind.AND))
                EmitUtil.emitAND(tree,arg,this);
            else if(opcode.equals(TokenKind.OR))
                EmitUtil.emitOR(tree,arg,this);
            else {
                int code = OpCodeSelecter.getIntCompareOpCode(opcode);
                EmitUtil.emitCompare(tree,arg,this,code);
            }
        }
        else {
            /* 算术运算 */
            emit(tree.left, arg);
            emit(tree.right, arg);
            int op = EmitUtil.getBinaryOpCode(tree);
            arg.mv.visitInsn(op);
        }
    }

    /** 生成变量声明表达式 */
    @Override
    public void visitVarDef(JCVariableDecl tree, EmitContext arg)
    {
        if(tree.init!=null) {
            emit(tree.init, arg);
            BVarSymbol varSymbol = (BVarSymbol) tree.nameExpr.symbol;
            emitStoreVar(arg, varSymbol);
        }
    }

    /** 保存变量 */
    private void emitStoreVar(EmitContext arg , BVarSymbol varSymbol ) {
        if(varSymbol.varKind.equals(VarSymbolKind.field))
        {
            EmitUtil.emitStoreField(arg, varSymbol);
        }
        else   if(varSymbol.varKind.equals(VarSymbolKind.parameter)||varSymbol.varKind.equals(VarSymbolKind.localVar))
        {
            DVarSymbol declVarSymbol = (DVarSymbol) varSymbol;
            int op = OpCodeSelecter.getStoreOpCode(declVarSymbol.getTypeSymbol());
            arg. mv.visitVarInsn(op, declVarSymbol.adr);
        }
        else
            throw new CompileError();
    }

    /** 生成赋值表达式 */
    @Override
    public void visitAssign(JCAssign tree, EmitContext arg) {
        //if(tree.left instanceof JCArrayAccess)
       // {
       //     JCArrayAccess jcArrayAccess = (JCArrayAccess)tree.left;
            /* 生成数组变量表达式 */
       //     emit(jcArrayAccess.indexed,arg);
            /* 生成数组索引表达式 */
       //     emit(jcArrayAccess.index,arg);
            /* 生成右边的值 */
       //     emit( tree.right,arg);
            /* 根据数组元素类型取出对应的指令并生成 */
       //     int arrayStormOp = OpCodeSelecter.arrayStorm(tree.left.symbol.getTypeSymbol());
       //     arg.mv.visitInsn(arrayStormOp);
     //   }
        //else
            if(tree.left instanceof JCFieldAccess)
        {
            JCFieldAccess jcFieldAccess = (JCFieldAccess)tree.left;
            /* 生成左边被赋值的表达式 */
            emit(jcFieldAccess.selected,arg);
            /* 生成右边的值 */
            emit(tree.right,arg);
            /* 生成保存字段指令 */
            EmitUtil. emitStoreField(arg,(BVarSymbol) jcFieldAccess.symbol);
        }
        else if(tree.left instanceof JCIdent)
        {
            emit(tree.right,arg);
            emitStoreVar(arg,(BVarSymbol) tree.left.symbol );
        }
        else
        {
            throw new CompileError();
        }
    }

    /** 生成访问数组元素表达式 */
   /* @Override
    public void visitArrayAccess(JCArrayAccess tree, EmitContext arg) {*/
        /* 生成数组变量表达式 */
      //  emit(tree.indexed,arg);
        /* 生成数组索引表达式 */
       // emit(tree.index,arg);
        /* 根据数组元素类型取出对应的指令并生成 */
      /*  BTypeSymbol elementSymbol = SymbolUtil.getElementType(tree.indexed.symbol);
        int op = OpCodeSelecter.arrayLoad(elementSymbol);
        arg.mv.visitInsn(op);
    }
    */
    /** 生成函数调用表达式 */
    @Override
    public void visitMethodInvocation(JCMethodInvocation tree, EmitContext arg) {
        /* 生成函数名称表达式 */
        emit( tree.methodExpr,arg);
        /* 生成参数 */
        for (JCExpression jcExpression : tree.args)
            emit(jcExpression,arg);

        BMethodSymbol methodSymbol = (BMethodSymbol) tree.methodExpr.symbol;
        /* 生成函数所属类型签名 */
        String owner = SymbolSignatureUtil.getParamsSignature(methodSymbol.ownerType,false);
        /* 获取函数名称 */
        String name = methodSymbol.name;
        /* 生成函数的返回类型的签名 */
        String returnSign =SymbolSignatureUtil.getParamsSignature( methodSymbol,true);
        /* 判断调用指令 */
        boolean isInterface = methodSymbol.ownerType.getTypeSymbol().isInterface;
        int invokeOp ;
        if (methodSymbol.isStatic)
            invokeOp = INVOKESTATIC;
        else if(isInterface)
            invokeOp = INVOKEINTERFACE;
        else
            invokeOp = INVOKEVIRTUAL;
        /* 生成函数调用 */
        arg. mv.visitMethodInsn(invokeOp, owner, name, returnSign, isInterface);
    }

    /** 生成创建实例表达式 */
    // @Override
    //public void visitNewClass(JCNewClass tree, EmitContext arg) {
        /* 生成 NEW + 实例类型的签名 */
        // BMethodSymbol initSymbol =tree.constructorSymbol;
        // String sign =SymbolSignatureUtil.getParamsSignature( initSymbol.returnType,false);
        // arg.mv.visitTypeInsn(NEW,sign );
        /* 生成 DUP 指令 */
        //  arg.mv.visitInsn(DUP);
        /* 生成参数 */
        //  for (JCExpression jcExpression : tree.args)
            //      emit(jcExpression,arg);
        /* 获取构造函数的签名 */
        //   String msign = SymbolSignatureUtil.getParamsSignature( initSymbol ,true);
        /* 生成 INVOKESPECIAL ,调用类型的构造函数进行初始*/
        //    arg.mv.visitMethodInsn(INVOKESPECIAL, SymbolSignatureUtil.getParamsSignature( initSymbol.ownerType,false),"<init>",msign , false);
        //}

    /** 生成创建数组表达式*/
  //  @Override
   // public void visitNewArray(JCNewArray tree, EmitContext arg) {
        /* 生成数组长度表达式 */
       // emit(tree.lengthExpr,arg);
        /* 获取元素类型对应指令 */
      /*  int op =-1 ;
        BTypeSymbol elementSymbol =  tree.elemtype.symbol.getTypeSymbol();
        if(elementSymbol instanceof RClassSymbol)
        {
            RClassSymbol typeSymbol =  (RClassSymbol)elementSymbol;
            op = OpCodeSelecter.newArray(typeSymbol.clazz);
        }*/
     //   if(op!=-1)
            /* 非-1,表示它是基本数据类型，有专门的JVM指令 */
           // arg.mv.visitIntInsn(NEWARRAY, op);
     //   else
            /* 否则是对象类型,根据签名生成 */
        /*    arg.mv.visitTypeInsn(ANEWARRAY, SymbolSignatureUtil.getParamsSignature( elementSymbol,false));
    }*/

    /** 生成括号表达式 */
    @Override
    public void visitParens(JCParens tree, EmitContext arg) {
        emit(tree.expr,arg);
    }

    void emit(JCTree tree, EmitContext arg) {
        if (tree == null) return;
        tree.scan(this, arg);
    }
}
