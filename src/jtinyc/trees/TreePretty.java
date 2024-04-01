package jtinyc.trees;

import java.io.*;
import java.util.ArrayList;

public class TreePretty extends TreeScanner<Object>  {
    public static final int
            notExpression = -1,   // not an expression
            noPrec = 0,           // no enclosing expression
            assignPrec = 1,
            postfixPrec = 15;

    public TreePretty(Writer out ) {
        this.out = out;
    }

    /** The output stream on which trees are printed.
     */
    Writer out;

    /** Indentation width (can be reassigned from outside).
     */
    public int width = 4;

    /** The current left margin.
     */
    int lmargin = 0;

    /** Align code to be indented to left margin.
     */
    void align() {
        for (int i = 0; i < lmargin; i++) print(" ");
    }

    /** Increase left margin by indentation width.
     */
    void indent() {
        lmargin = lmargin + width;
    }

    /** Decrease left margin by indentation width.
     */
    void undent() {
        lmargin = lmargin - width;
    }

    /** Enter a new precedence level. Emit a `(' if new precedence level
     *  is less than precedence level so far.
     *  @param contextPrec    The precedence level in force so far.
     *  @param ownPrec        The new precedence level.
     */
    void open(int contextPrec, int ownPrec)  {
        if (ownPrec < contextPrec) print("(");
    }

    /** Leave precedence level. Emit a `(' if inner precedence level
     *  is less than precedence level we revert to.
     *  @param contextPrec    The precedence level we revert to.
     *  @param ownPrec        The inner precedence level.
     */
    void close(int contextPrec, int ownPrec)  {
        if (ownPrec < contextPrec) print(")");
    }

    private void print(Object s) {
        try {
            out.write(s.toString());
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    /** Print new line.
     */
    public void println() {
       // out.write(lineSep);
        print(lineSep);
    }

    String lineSep = System.getProperty("line.separator");


    /** Visitor argument: the current precedence level.
     */
    int prec;

    public void printTree(JCTree tree, int prec)
    {
        if(tree==null) return;
        int prevPrec = this.prec;
        try {
            this.prec = prec;
            if (tree == null) print("/*missing*/");
            else {
                tree.scan(this,null);
            }
        } catch (Error ex) {
            IOException e = new IOException(ex.getMessage());
            System.out.println(ex.getMessage());
           // e.initCause(ex);
            //throw e;
        } finally {
            this.prec = prevPrec;
        }
    }

    /** Derived visitor method: print expression tree at minimum precedence level
     *  for expression.
     */
    public void printTree(JCTree tree) {
        if(tree!=null)
            printTree(tree, noPrec);
    }

    /** Derived visitor method: print statement tree.
     */
    public void printStat(JCTree tree)  {
        printTree(tree, notExpression);
    }

    public <T extends JCTree> void printExprs(ArrayList<T> trees, String sep)  {
        if (trees.size()>0) {
            printTree(trees.get(0));
            for (int i=1;i<trees.size();i++) {
                print(sep);
                printTree(trees.get(i));
            }
        }
    }

    public <T extends JCTree> void printExprs(ArrayList<T> trees)  {
        printExprs(trees, ", ");
    }

    /** Derived visitor method: print list of statements, each on a separate line.
     */
    public void printStats(ArrayList<? extends JCTree> trees)  {
        for(int i=0;i<trees.size();i++)
        {
            align();
            printStat(trees.get(i));
            println();
        }
    }

    /** Print a block.
     */
    public void printBlock(ArrayList<? extends JCTree> stats)  {
        print("{");
        println();
        indent();
        printStats(stats);
        undent();
        align();
        print("}");
    }

    public void visitFile(JCFile tree, Object arg) {
            if(tree.packageDecl !=null)
                visitPackage(tree.packageDecl,null);
            for(int i=0;i<tree.imports.size();i++)
            {
                visitImport(tree.imports.get(i),null);
            }
            for(int i=0;i<tree.methods.size();i++)
            {
                visitMethodDef(tree.methods.get(i),null);
            }
    }

    public void visitPackage(JCPackage tree, Object arg) {
            if (tree.packageName != null) {
                print("package ");
                print(tree.packageName.getFullName());
                print(";");
                println();
            }
    }

    public void visitImport(JCImport tree, Object arg) {
        if (tree.typeTree != null) {
            print("import ");
            print(tree.typeTree.getFullName());
            print(";");
            println();
        }
    }

    public void visitMethodDef(JCMethod tree, Object arg) {
        println();
        align();
        visitMethodDefHead(tree);
        printStat(tree.body);
    }

    public void visitMethodDefHead(JCMethod tree) {
        println();
        align();
        printTree(tree.retTypeExpr);
        print(" " + tree.nameToken.identName);
        print("(");
        printExprs(tree.params);
        print(")");
    }

    public void visitVarDef(JCVariableDecl tree, Object arg) {
        printTree(tree.varType);
        print(" " + tree.nameExpr.getName());
        if (tree.init != null) {
            print(" = ");
            printTree(tree.init);
        }
    }

    public void visitBlock(JCBlock tree, Object arg) {
            printBlock(tree.statements);
    }

    public void visitWhileLoop(JCWhile tree, Object arg) {
        print("while ");
        print("(");
        printTree(tree.cond);
        print(")");
        print(" ");
        printStat(tree.body);
    }
/*
    public void visitForLoop(JCForLoop tree, Object arg) {
        print("for (");
        printTree(tree.init);

        printTree(tree.cond);
        print("; ");

        tree.step.scan(this, arg);
        print(") ");
        printStat(tree.body);
    }*/

    public void visitIf(JCIf tree, Object arg) {
        print("if ");
        print("(");
        printTree(tree.cond);
        print(")");
        print(" ");
        printStat(tree.thenpart);
        if (tree.elsepart != null) {
            print(" else ");
            printStat(tree.elsepart);
        }
    }

    public void visitExprStmt(JCExprStatement tree, Object arg) {
        printTree(tree.expr);
        print(";");
    }
/*
    public void visitBreak(JCBreak tree, Object arg) {
        try {
            print("break;");
        } catch (IOException e) {
            throw new Error(e);
        }
    }*/
/*
    public void visitContinue(JCContinue tree, Object arg) {
        try {
            print("continue");
           // if (tree.label != null) print(" " + tree.label);
            print(";");
        } catch (IOException e) {
            throw new Error(e);
        }
    }*/

    public void visitReturn(JCReturn tree, Object arg) {
        print("return");
        if (tree.expr != null) {
            print(" ");
            printTree(tree.expr);
        }
        print(";");
    }

    public void visitMethodInvocation(JCMethodInvocation tree, Object arg) {
        printTree(tree.methodExpr);
        print("(");
        printExprs(tree.args);
        print(")");
    }

   /* public void visitNewClass(JCNewClass tree, Object arg) {
        print("new ");
        printTree(tree.clazzExpr);
        print("(");
        printExprs(tree.args);
        print(")");
    }*/
/*
    public void visitNewArray(JCNewArray tree, Object arg) {
        print("new ");
        printTree(tree.elemtype);
        print("[");
        printTree(tree.lengthExpr);
        print("]");
    }*/

    public void visitParens(JCParens tree, Object arg) {
        print("(");
        printTree(tree.expr);
        print(")");
    }

    public void visitAssign(JCAssign tree, Object arg) {
        open(prec, assignPrec);
        printTree(tree.left, assignPrec + 1);
        print(" = ");
        printTree(tree.right, assignPrec);
        close(prec, assignPrec);
    }

    public void visitUnary(JCUnary tree, Object arg) {
        print(tree.opcode.name);
        printTree(tree.expr);
    }

    public void visitBinary(JCBinary tree, Object arg) {
        printTree(tree.left);
        print(tree.opcode.name);
        printTree(tree.right);
    }
/*
    public void visitArrayAccess(JCArrayAccess tree, Object arg) {
        printTree(tree.indexed, postfixPrec);
        print("[");
        printTree(tree.index);
        print("]");
    }
*/
    /*
    public void visitFieldAccess(JCFieldAccess tree, Object arg) {
        printTree(tree.selected, postfixPrec);
        print(".");
        if (tree.nameToken != null)
            print(tree.nameToken.identName);
    }
*/
    public void visitIdent(JCIdent tree, Object arg) {
        print(tree.getName());
    }

    public void visitLiteral(JCLiteral tree , Object arg) {
        if (tree.value != null)
            print(tree.value);
    }

    public void visitPrimitiveType(JCPrimitiveType tree, Object arg) {
            switch(tree.kind)
            {
                case INT:
                    print("int");
                    break;
                case BOOLEAN:
                    print("boolean");
                    break;
                case VOID:
                    print("void");
                    break;
                default:
                    print("<error>");
                    break;
            }
    }
/*
    public void visitArrayType(JCArrayTypeTree tree, Object arg) {
            printTree(tree.elemType);
            print("[]");
    }*/

    @Override
    public void visitTree(JCTree tree, Object arg) {
            print("(UNKNOWN: " + tree.getClass().getSimpleName() + ")");
            println();
    }
}
