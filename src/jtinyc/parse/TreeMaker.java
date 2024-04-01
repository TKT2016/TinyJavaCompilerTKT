/*
 * Jtiny compiler source code
 * License : the GNU General Public License
 * Copyright (C) 2021-2024 Chines JSSQSY WANG
 */
package jtinyc.parse;

import jtinyc.utils.SourceLog;
import jtinyc.lex.Token;
import jtinyc.trees.*;
import jtinyc.lex.TokenKind;

import java.util.ArrayList;

import jtinyc.trees.JCTree;
import tools.StringHelper;

/** 抽象语法树工厂(包含检查树的完整性) */
class TreeMaker
{
    public SourceLog log;

    /** 当前位置 */
    public int pos= -1;//SourceLog.NOPOS;

    /* 当前行号*/
    public int line = 1 ;

    Token posToken;

    public TreeMaker(SourceLog log ) {
        this.log = log;
    }

    /** 给pos和line赋值并返回自己以便连续调用 */
   /* public TreeMaker at(int pos,int line) {
        this.pos = pos;
        this.line = line;
        return this;
    }*/

    public TreeMaker at(Token token) {
        this.posToken = token;
        this.pos = token.pos;
        this.line = token.line;
        return this;
        //return at(token.pos,token.line);
    }

    void initTree(JCTree tree)
    {
        tree.log = log;
        tree.pos = pos;
        tree.line = line;
    }

    void error(String msg)
    {
       // this.log.error(pos,line,msg,1);
        this.log.error(posToken,msg);
    }

    /** 创建JCPackage */
    public JCPackage PackageDecl(JCFullname pid) {
        JCPackage tree = new JCPackage(  );
        tree.packageName = pid;
        initTree(tree);
        return tree;
    }

    /** 创建JCImport */
    public JCImport Import(JCFullname typeTree) {
        JCImport tree = new JCImport();
        tree.typeTree = typeTree;
        initTree(tree);
        if(!check(tree))
            return null;
        else
            return tree;
    }

    boolean check(JCImport tree)
    {
        boolean right= true;
        if(tree.typeTree ==null)
        {
            error("import缺少类名称或包名称");
            right = false;
        }
        return right;
    }

    /* 创建JCClassDecl */
    /*public JCClassDecl ClassDef(String name, ArrayList<JCTree> defs)
    {
        JCClassDecl tree = new JCClassDecl(name, defs);
        initTree(tree);
        check(tree);
        return tree;
    }*/

   /* boolean check( JCClassDecl tree)
    {
        boolean error=false;
        if(StringHelper.isNullOrEmpty(tree.name))
        {
            error("import缺少类名称或包名称");
            error = true;
        }
        return !error;
    }*/

    /* 创建JCMethodDecl */
    public JCMethod MethodDef( JCExpression retType,Token nameToken,
                               ArrayList<JCVariableDecl> params, JCBlock body)
    {
        JCMethod tree = new JCMethod( );
        tree.nameToken = nameToken;
        tree.retTypeExpr = retType;
        tree.params = params;
        tree.body = body;
        initTree(tree);
        check(tree);
        return tree;
    }

    boolean check( JCMethod tree)
    {
        boolean right= true;
        if(tree.retTypeExpr== null ) {
            error("函数缺少返回值");
           right =false;
        }
        if( tree.nameToken== null
                ||  StringHelper.isNullOrEmpty( tree.nameToken.identName)) {
            error("函数名称不能为空");
            right =false;
        }
        if( tree.params== null ) {
            error("函数缺少形参");
            right =false;
        }
        if( tree.body== null ) {
            error("缺少函数体");
            right =false;
        }
        return right;
    }

    /** 创建类型声明 JCVariableDecl */
    public JCVariableDecl VarDef(JCExpression varType,JCIdent nameExpr,  JCExpression init ,boolean isLocalVar) {
        JCVariableDecl tree = new JCVariableDecl( );
        tree.varType = varType;
        tree.nameExpr = nameExpr;
        tree.init = init;
        initTree(tree);
        check(tree,isLocalVar);
        return tree;
    }

    boolean check( JCVariableDecl tree,boolean isLocalVar)
    {
        boolean right= true;
        if( tree.varType ==null)
        {
            error("变量缺少声明类型");
            right =false;
        }
        if( tree.nameExpr ==null)
        {
            error("变量缺少名称");
            right =false;
        }
        if(isLocalVar && tree.init==null)
        {
            error("声明局部变量时必须赋值");
            right =false;
        }
        return right;
    }

    /** 创建语句块JCBlock */
    public JCBlock Block( ArrayList<JCStatement> stats) {
       /*  for(JCTree stmt:tree.stats)
        {
            if(stmt instanceof JCBlock)
            {
                stmt.error("代码块内禁止嵌套代码块");
            }
            stmt.scan(this,context);
        }*/
        JCBlock tree = new JCBlock( );
        tree.statements = stats;
        initTree(tree);
        return tree;
    }

    /** 创建while循环 */
    public JCWhile WhileLoop(JCExpression cond, JCStatement body) {
        JCWhile tree = new JCWhile( );
        tree.cond = cond;
        tree.body = body;
        initTree(tree);
        check(tree);
        return tree;
    }

    boolean check(JCWhile tree)
    {
        boolean right =true;
        if(tree.cond==null) {
            error("while循环语句缺少条件表达式");
            right =false;
        }
        if(tree.body==null) {
            error("while循环语句缺少循环体");
            right =false;
        }
        return right;
    }

    /** 创建JCForLoop循环 */
  /*  JCForLoop ForLoop()
    {
        JCForLoop tree = new JCForLoop();
        initTree(tree);
        return tree;
    }

    boolean check(JCForLoop tree)
    {
        boolean right =true;
        if(tree.init!=null) {
            if(tree.init instanceof JCReturn) {
                tree.init.error("for循环语句初始化禁止使用return语句");
                right =false;
            }
        }

        if(tree.body==null) {
            error("for循环语句缺少循环体");
            right =false;
        }
        return right;
    }*/

    /** 创建JCForLoop循环 */
    /*public JCForLoop ForLoop(JCStatement init,
                             JCExpression cond,
                             JCExpression step,
                             JCStatement body)
    {
        JCForLoop tree = new JCForLoop(init, cond, step, body);
        initTree(tree);
        return tree;
    }*/

    /** 创建JCIf */
    public JCIf If(JCExpression cond, JCStatement thenpart, JCStatement elsepart) {
        if(cond==null) {
            error("if语句缺少条件表达式");
            return null;
        }
        if(thenpart==null)
        {
            error("if语句缺少执行语句");
            return null;
        }
        JCIf tree = new JCIf();
        tree.cond = cond;
        tree.thenpart = thenpart;
        tree.elsepart = elsepart;
        initTree(tree);
        check(tree);
        return tree;
    }

    boolean check(JCIf tree)
    {
        boolean right =true;
        if(tree.cond==null) {
            error("if语句缺少条件表达式");
            right =false;
        }
        if(tree.thenpart==null)
        {
            error("if语句缺少执行语句");
            right =false;
        }
        return right;
    }

    /* 创建表达式语句 JCExpressionStatement */
    public JCExprStatement Exec(JCExpression expr) {
        JCExprStatement tree = new JCExprStatement();
        tree.expr = expr;
        initTree(tree);
        if(!check(tree))
            return null;
        return tree;
    }

    boolean check(JCExprStatement tree)
    {
        boolean right =true;
        JCExpression expr = tree.expr;
        if(expr==null)
        {
            error("语句缺少表达式");
            right =false;
        }
        else if(!(
                expr instanceof JCAssign
                        || expr instanceof JCVariableDecl
                        || expr instanceof JCMethodInvocation
                        //|| expr instanceof JCNewClass
                ))
        {
            error("不是正确的表达式语句");
            right =false;
        }
        return right;
    }

    /** 创建JCBreak语法树 */
   /* public JCBreak Break( ) {
        JCBreak tree = new JCBreak();
        initTree(tree);
        return tree;
    }*/

    /** 创建JCContinue语法树 */
  /*  public JCContinue Continue() {
        JCContinue tree = new JCContinue();
        initTree(tree);
        return tree;
    }*/

    /** 创建JCReturn语法树 */
    public JCReturn Return(JCExpression expr) {
        JCReturn tree = new JCReturn();
        tree.expr = expr;
        initTree(tree);
        return tree;
    }

    public JCMethodInvocation Apply(JCExpression meth, ArrayList<JCExpression> args)
    {
        JCMethodInvocation tree = new JCMethodInvocation( );
        tree.methodExpr = meth;
        tree.args = args;
        initTree(tree);
        return tree;
    }

   /* public JCNewClass NewClass(JCIdent clazzExpr, ArrayList<JCExpression> args)
    {
        JCNewClass tree =new JCNewClass(  ) ;
        tree.clazzExpr = clazzExpr;
        tree.args = args;
        //this.args.addAll(args);
        initTree(tree);
        return tree;
    }*/
/*
    public JCNewArray NewArray(JCExpression elemtype)
    {
        JCNewArray tree = new JCNewArray();
        tree.elemtype = elemtype;
        //this.lengthExpr = dims;
        initTree(tree);
        check(tree); //if(!check(tree)) return null;
        return tree;
    }*/
/*
    public boolean check(JCNewArray tree)
    {
        boolean right =true;
        if(tree.elemtype==null)
        {
            error("创建数组表达式缺少类型");
            right =false;
        }
        return right;
    }*/

    public JCParens Parens(JCExpression expr) {
        JCParens tree = new JCParens();
        tree.expr = expr;
        initTree(tree);
        check(tree); //if(!check(tree)) return null;
        return tree;
    }

    public boolean check(JCParens tree)
    {
        boolean right =true;
        if(tree.expr==null)
        {
            error("括号内缺少表达式");
            right =false;
        }
        return right;
    }

    /** 创建赋值表达式 */
    public JCAssign Assign(JCExpression lhs, JCExpression rhs) {
        JCAssign tree = new JCAssign( );
        tree.left = lhs;
        tree.right = rhs;
        initTree(tree);
        check(tree); //if(!check(tree)) return null;
        return tree;
    }

    boolean check(JCAssign tree)
    {
        boolean right =true;
        if(tree.left==null)
        {
            error("赋值语句左边缺少表达式");
            right =false;
        }

        if(tree.right ==null) {
            error("赋值语句右边缺少表达式");
            right =false;
        }
        return right;
    }

    public JCUnary Unary(TokenKind opcode, JCExpression arg) {
        JCUnary tree = new JCUnary( );
        tree.opcode = opcode;
        tree.expr = arg;
        initTree(tree);
        check(tree); //if(!check(tree)) return null;
        return tree;
    }

    boolean check(JCUnary tree)
    {
        boolean right =true;
        TokenKind opcode = tree.opcode;
        if(!(opcode== TokenKind.ADD ||opcode== TokenKind.SUB ||opcode== TokenKind.NOT))
        {
            error("单目表达式前缀只能为'+','-','!'");
            right =false;
        }

        if( tree.expr==null)
        {
            error("单目后边缺少表达式");
            right =false;
        }
        return right;
    }

    public JCExpression Binary(TokenKind opcode, JCExpression lhs, JCExpression rhs) {
        if(lhs!=null) {
            JCBinary tree = new JCBinary();
            tree.opcode = opcode;
            tree.left = lhs;
            tree.right = rhs;
            initTree(tree);
            check(tree); //if(!check(tree)) return null;
            return tree;
        }
        else
        {
            return Unary(opcode,rhs);
        }
    }

    boolean check(JCBinary tree)
    {
        boolean right =true;
        if( tree.left ==null)
        {
            error("二元运算表达式左边缺少表达式");
            right =false;
        }
        if( tree.right ==null)
        {
            error("二元运算表达式右边缺少表达式");
            right =false;
        }
        return right;
    }
/*
    public JCArrayAccess ArrayAccess(JCExpression indexed, JCExpression index) {
        JCArrayAccess tree = new JCArrayAccess( );
        tree.indexed = indexed;
        tree.index = index;
        initTree(tree);
        check(tree); //if(!check(tree)) return null;
        return tree;
    }
*/
    /*
    boolean check(JCArrayAccess tree)
    {
        boolean right =true;
        if(tree.indexed==null)
        {
            error("数组访问表达式左边缺少数组");
            right =false;
        }

        if(tree.index==null) {
            error("数组访问表达式左边缺少索引");
            right =false;
        }
        return right;
    }
*/

    public JCFieldAccess FieldAccess(JCExpression selected, Token selector) {
        JCFieldAccess tree = new JCFieldAccess(  );
        tree.selected = selected;
        tree.nameToken = selector;
        initTree(tree);
        check(tree);
        return tree;
    }

    boolean check(JCFieldAccess tree)
    {
        boolean right =true;
        if(tree.selected ==null) {
            error("缺少被限定名称");
            right =false;
        }
        if(tree.nameToken==null || StringHelper.isNullOrEmpty(tree.nameToken.identName))
        {
            error("缺少限定名称");
            right =false;
        }
        return right;
    }

    public JCIdent Ident(Token nameToken) {
        if(nameToken==null ||   StringHelper.isNullOrEmpty(nameToken.identName))
        {
            error("标识符不能为空");
            return  null;
        }
        JCIdent tree = new JCIdent();
        tree.nameToken = nameToken;
        initTree(tree);
        check(tree); //if(!check(tree)) return null;
        return tree;
    }

    boolean check(JCIdent tree)
    {
        boolean right =true;
        if(tree.nameToken ==null ||   StringHelper.isNullOrEmpty(tree.nameToken.identName)) {
            error("标识符不能为空");
            right =false;
        }
        return right;
    }

    public JCLiteral Literal(Object value) {
        JCLiteral tree = new JCLiteral( value);
        initTree(tree);
        return tree;
    }

    /** 创建基本类型语法树实例 */
    public JCPrimitiveType PrimitiveType(TokenKind kind) {
        JCPrimitiveType tree = new JCPrimitiveType( );
        tree.kind = kind;
        initTree(tree);
        return tree;
    }
/*
    public JCArrayTypeTree TypeArray(JCExpression elemtype) {
        JCArrayTypeTree tree = new JCArrayTypeTree();
        tree.elemType = elemtype;
        initTree(tree);
        check(tree); //if(!check(tree)) return null;
        return tree;
    }*/
/*
    boolean check(JCArrayTypeTree tree)
    {
        boolean right =true;
        if(tree.elemType ==null)
        {
            error("数组声明缺少类型");
            right =false;
        }
        return right;
    }*/

/*
    public JCErroneous Erroneous() {
        return Erroneous(new ArrayList<>());
    }*/
/*
    public JCErroneous Erroneous(ArrayList<? extends JCTree> errs) {
        JCErroneous tree = new JCErroneous();
        tree.errs  = errs;
        tree.log= log;
        tree.pos = pos;
        tree.line = line;
        return tree;
    }*/
}
