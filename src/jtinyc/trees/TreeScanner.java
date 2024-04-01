package jtinyc.trees;

public abstract class TreeScanner<T>
{
    public void visitFile(JCFile compilationUnit  , T arg) {
        if(compilationUnit.packageDecl !=null)
            compilationUnit.packageDecl.scan(this,arg);

        for(int i=0;i<compilationUnit.imports.size();i++)
        {
            compilationUnit.imports.get(i).scan(this,arg);
        }

        for(int i=0;i<compilationUnit.methods.size();i++)
        {
            compilationUnit.methods.get(i).scan(this,arg);
        }
    }

    public void visitPackage(JCPackage jcPackageDecl, T arg)
    {
        return;
    }

    public void visitImport(JCImport tree, T arg)
    {
        return;
    }

    public void visitMethodDef(JCMethod tree, T arg)
    {
        visitTree(tree.retTypeExpr,arg);
        for(JCVariableDecl param:tree.params)
        {
            visitTree(param,arg);
        }
        tree.body.scan(this,arg );
    }

    public void visitVarDef(JCVariableDecl tree, T arg) {
        visitTree(tree.varType,arg);
        visitTree(tree.init,arg);
    }

    public void visitBlock(JCBlock tree,  T arg) {
        for(JCTree stmt:tree.statements)
        {
            visitTree(stmt,arg);
        }
    }
   
    public void visitWhileLoop(JCWhile tree, T arg)
    {
        visitTree(tree.cond,arg);
        visitTree(tree.body,arg);
    }
/*
    public void visitForLoop(JCForLoop tree, T arg)
    {
        visitTree(tree.init,arg);
        visitTree(tree.cond,arg);
        visitTree(tree.step,arg);
        visitTree(tree.body,arg);
    }*/

    public void visitIf(JCIf tree, T arg)
    {
        visitTree(tree.cond,arg);
        visitTree(tree.thenpart,arg);
        visitTree(tree.elsepart,arg);
    }

    public void visitExprStmt(JCExprStatement tree, T arg)
    {
        visitTree(tree.expr,arg);
    }

    public void visitReturn(JCReturn tree, T arg)
    {
        visitTree(tree.expr,arg);
    }

    public void visitMethodInvocation(JCMethodInvocation tree, T arg)
    {
        visitTree(tree.methodExpr,arg);
        for(JCExpression expression:tree.args)
        {
            visitTree(expression,arg);
        }
    }

   /* public void visitNewClass(JCNewClass tree, T arg)
    {
        visitTree(tree.clazzExpr,arg);
        for(JCExpression expression:tree.args)
        {
            visitTree(expression,arg);
        }
    }*/
/*
    public void visitNewArray(JCNewArray tree, T arg)
    {
        visitTree(tree.elemtype,arg);
        visitTree(tree.lengthExpr,arg);
    }*/
   
    public void visitParens(JCParens tree, T arg)
    {
        visitTree(tree.expr,arg);
    }
   
    public void visitAssign(JCAssign tree, T arg)
    {
        visitTree(tree.left,arg);
        visitTree(tree.right,arg);
    }

    public void visitUnary(JCUnary tree, T arg)
    {
        visitTree(tree.expr,arg);
    }

    public void visitBinary(JCBinary tree, T arg)
    {
        visitTree(tree.left,arg);
        visitTree(tree.right,arg);
    }
   /*
    public void visitArrayAccess(JCArrayAccess tree, T arg)
    {
        visitTree(tree.indexed,arg);
        visitTree(tree.index,arg);
    }
*/
    public void visitFieldAccess(JCFieldAccess tree, T arg)
    {
        visitTree( tree.selected,arg);
    }

    public void visitIdent(JCIdent tree, T arg)
    {
        return;
    }

    public void visitLiteral(JCLiteral tree, T arg)
    {
       return;
    }

    public void visitPrimitiveType(JCPrimitiveType tree, T arg) {
        return;
    }
/*
    public void visitArrayType(JCArrayTypeTree tree, T arg)
    {
        visitTree(tree.elemType,arg);
    }
    */
/*
    public void visitErroneous(JCErroneous tree, T arg)
    {
        return;
    }*/

    protected void visitTree(JCTree tree,T arg)
    {
        if(tree==null) return;
        tree.scan(this,arg);
    }
}
