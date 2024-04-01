package jtinyc.trees;

import java.util.ArrayList;

public abstract class TreeTranslator<A>
{
    public JCFile translate(JCFile compilationFile)
    {
        translate(compilationFile,null);
        return compilationFile;
    }

    public JCTree translateFile(JCFile compilationFile, A arg)
    {
        for(int i=0;i<compilationFile.methods.size();i++)
            compilationFile.methods.get(i).translate(this,arg);
        return compilationFile;
    }
/*
    public JCTree translateArrayType(JCArrayTypeTree tree, A arg)
    {
        return tree;
    }*/

    public JCTree translatePrimitiveType(JCPrimitiveType tree, A arg)
    {
        return tree;
    }

    public JCTree translateMethod(JCMethod tree, A arg)
    {
        tree.body = (JCBlock) tree.body.translate(this,arg);
        return tree;
    }

    public JCTree translateBlock(JCBlock tree, A arg)
    {
        ArrayList<JCStatement> newStatements = new  ArrayList<JCStatement>();
        for (JCStatement statement : tree.statements)
        {
            JCTree jcTree =  statement.translate(this,arg);
            if(jcTree!=null)
            {
                newStatements.add((JCStatement)jcTree);
            }
        }
        tree.statements = newStatements;
        return tree;
    }

    protected <T extends JCTree> T translate(T tree, A arg)
    {
        if(tree==null) return null;
        JCTree newTree = tree.translate(this, arg);
        return (T) newTree;
    }

    public JCTree translateImport(JCImport tree, A arg)
    {
        return tree;
    }

    public JCTree translateAssign(JCAssign tree, A arg)
    {
        tree.left =translate(tree.left,arg);
        tree.right =translate(tree.right,arg);
        return tree;
    }
/*
    public JCTree translateNewArray(JCNewArray tree, A arg)
    {
        tree.elemtype = translate(tree.elemtype,arg);
        tree.lengthExpr =  translate(tree.lengthExpr,arg);
        return tree;
    }*/

    /*public JCTree translateNewClass(JCNewClass tree, A arg) {
        tree.clazzExpr = translate(tree.clazzExpr, arg);
        tree.args = translates(tree.args, arg);
        return tree;
    }*/

    public JCTree translatePackage(JCPackage tree, A arg)
    {
        return tree;
    }
/*
    public JCTree visitArrayAccess(JCArrayAccess tree, A arg) {
        tree.indexed = (JCExpression) tree.indexed.translate(this, arg);
        tree.index = (JCExpression) tree.index.translate(this, arg);
        return tree;
    }*/

    public JCTree translateFieldAccess(JCFieldAccess tree, A arg)
    {
        tree.selected = translate(tree.selected,arg);
        return tree;
    }

    public JCTree translateBinary(JCBinary tree, A arg)
    {
        tree.left =translate(tree.left,arg);
        tree.right =translate(tree.right,arg);
        return tree;
    }

    public JCTree translateIdent(JCIdent tree, A arg)
    {
        return tree;
    }

    public JCTree translateLiteral(JCLiteral tree, A arg)
    {
        return tree;
    }

    public JCTree translateParens(JCParens tree, A arg) {
        tree.expr = translate(tree.expr, arg);
        return tree;
    }

    public JCTree translateUnary(JCUnary tree, A arg) {
        tree.expr = translate(tree.expr, arg);
        return tree;
    }

    public JCTree translateReturn(JCReturn tree, A arg) {
        tree.expr = translate(tree.expr, arg);
        return tree;
    }

    public JCTree translateIf(JCIf tree, A arg) {
        tree.cond = translate(tree.cond, arg);
        tree.thenpart = translate(tree.thenpart, arg);
        tree.elsepart = translate(tree.elsepart, arg);
        return tree;
    }
/*
    public JCTree translateForLoop(JCForLoop tree, A arg) {
        tree.init = translate(tree.init, arg);
        tree.cond = translate(tree.cond, arg);
        tree.step = translate( tree.step,arg);
        tree.body = translate( tree.body,arg);
        return tree;
    }*/

    public JCTree translateWhile(JCWhile tree, A arg)
    {
        tree.cond = translate(tree.cond, arg);
        tree.body = translate( tree.body,arg);
        return tree;
    }

    public JCTree translateVariable(JCVariableDecl tree, A arg)
    {
        tree.init = translate( tree.init,arg);
        return tree;
    }

    public JCTree translateMethodInvocation(JCMethodInvocation tree, A arg)
    {
        tree.args = translates(tree.args,arg);
        return tree;
    }

    public JCTree translateExpressionStatement(JCExprStatement tree, A arg)
    {
        tree.expr = translate(tree.expr,arg);
        return tree;
    }

    protected <R extends JCTree> ArrayList<R> translates(ArrayList<R> trees, A arg)
    {
        ArrayList<R> defs = new  ArrayList<R>();
        for (R deftree : trees) {
            JCTree newTree = deftree.translate(this, arg);
            if (newTree != null) {
                R nr = (R) newTree;
                defs.add(nr);
            }
        }
        return defs;
    }
}
