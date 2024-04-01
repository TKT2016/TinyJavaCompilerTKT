package jtinyc.emits;

import jtinyc.symbols.*;
import jtinyc.trees.*;
import jtinyc.trees.TreeScanner;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

/** 函数内变量信息生成 */
public class LocalVariableEmit extends TreeScanner<MethodVisitor> {
    /* 变量信息生成 */
    public void emitVars(JCMethod tree, MethodVisitor mv) {
        visitMethodDef(tree, mv);
    }

    /** 设置变量符号的地址 */
    private void emitVarLabel(DVarSymbol varSymbol, MethodVisitor mv) {
        /* 变量名称 */
        String name = varSymbol.name;
        /* 变量类型签名 */
        String sign = SymbolSignatureUtil.getParamsSignature(varSymbol.varType, true);
        /* 变量作用域开始标签 */
        Label startLabel= varSymbol.scope.startLabel;
        /* 变量作用域结束标签 */
        Label endLabel= varSymbol.scope.endLabel;
        /* 变量地址 */
        int adr = varSymbol.adr;
        mv.visitLocalVariable(name, sign, null, startLabel, endLabel, adr);
    }

    /** 扫描函数*/
    @Override
    public void visitMethodDef(JCMethod tree, MethodVisitor mv) {
        DMethodSymbol declMethodSymbol = tree.methodSymbol;
        /* 按参数顺序依次给参数地址赋值 */
        for (int i = 0; i < declMethodSymbol.getParameterCount(); i++) {
            DVarSymbol varSymbol = declMethodSymbol.getParameterSymbol(i);
            emitVarLabel(varSymbol, mv);
        }
        /* 扫描函数体变量地址 */
        tree.body.scan(this, mv);
    }

    /** 扫描变量声明表达式 */
    @Override
    public void visitVarDef(JCVariableDecl tree, MethodVisitor mv) {
        emitVarLabel((DVarSymbol) tree.nameExpr.symbol, mv);
    }

    /**
     * 扫描代码块
     */
    @Override
    public void visitBlock(JCBlock tree, MethodVisitor mv) {
        for (JCTree stmt : tree.statements) {
            if (stmt instanceof JCExprStatement
                    || stmt instanceof JCIf
                  //  || stmt instanceof JCForLoop
                    || stmt instanceof JCWhile
                    || stmt instanceof JCBlock)
                stmt.scan(this, mv);
        }
    }

    /**
     * 扫描for循环
     */
    /*@Override
    public void visitForLoop(JCForLoop tree, MethodVisitor mv) {
        if (tree.init != null)
            tree.init.scan(this, mv);
        tree.body.scan(this, mv);
    }*/

    /**
     * 扫描表达式语句
     */
    @Override
    public void visitExprStmt(JCExprStatement tree, MethodVisitor mv) {
        if (tree.expr instanceof JCVariableDecl)
            tree.expr.scan(this, mv);//只扫描变量声明表达式
    }
}
