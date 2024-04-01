/*
 * Jtiny compiler source code
 * License : the GNU General Public License
 * Copyright (C) 2021-2024 Chines JSSQSY WANG
 */
package jtinyc.parse;

import jtinyc.lex.Tokenizer;
import jtinyc.lex.Token;
import jtinyc.lex.TokenKind;
import jtinyc.trees.*;
import jtinyc.utils.CompileError;
import jtinyc.utils.SourceLog;
import java.util.ArrayList;
import static jtinyc.lex.TokenKind.*;

/** 语法分析器 */
public class Parser {
   /* 词法分析器 */
    Tokenizer tokenizer;
    /* 当前词法标记 */
    Token token;
    /* 当前词法标记类型 */
    TokenKind kind;
    /* 错误提示 */
    SourceLog log;
    /* 语法树建造器 */
    TreeMaker treeMaker;

    public Parser( Tokenizer tokenizer, SourceLog log) {
        this.tokenizer = tokenizer;
        this.log = log;
        this.treeMaker = new TreeMaker(log);
        /* 读取第一个Token */
        nextToken();
    }
    /** 读取下一个Token */
    void nextToken() {
        token = tokenizer.readToken();
        kind = token.kind;
    }
    /** 验证当前token的类型是否与tk相同 */
    void accept(TokenKind tk) {
        if(token.kind == tk)
            nextToken();
        else
        {
            String msg;
            if (tk.name != null)
                msg = "期望是 '" + tk.name + "' ";
            else if (tk == TokenKind.IDENTIFIER)
                msg ="期望是<标识符>";
            else
                msg ="期望是<" + tk + ">";
            log.error(token,msg);
        }
    }
    /** 报错 */
    void error(Token posToken,String msg)
    {
        this.log.error(posToken,msg);
    }

    /** 分析源文件语法树
     * File ::=  Package  {Import} {Method}
     */
    public JCFile parseFile() {
        JCFile fileTree = new JCFile();
        fileTree.log = log;
        fileTree.imports = new ArrayList<>();
        fileTree.methods = new ArrayList<>();

        try {
            /* 分析 包声明 */
            if (kind == TokenKind.PACKAGE)
                fileTree.packageDecl = parsePackage();
            /* 必须声明包名称，此处检查是否声明了 */
            if(fileTree.packageDecl==null)
                log.error("缺少包声明");

            /* 分析 import */
            while (kind == TokenKind.IMPORT) {
                JCImport importTree = parseImport();
                if (importTree != null && importTree.typeTree!=null) //检查importTree是否有效
                    fileTree.imports.add(importTree);
            }

            /* 分析函数 */
            while (kind != TokenKind.EOF) {
                JCMethod def = parseMethod();
                if (def != null)
                    fileTree.methods.add(def);
            }
        }
        catch (ParseEOFError ex){
        }
        return fileTree;
    }

    /** 分析PACKAGE语句
     * Package ::= 'package' Fullname;"
     */
    JCPackage parsePackage()
    {
        /* 记录开始词法标记 */
        Token posToken = token;
        /* 验证关键字PACKAGE */
        accept(PACKAGE);
        /* 分析包名 */
        JCFullname packageName = parseFullName();
        /* 验证末尾是否是分号*/
        accept(TokenKind.SEMI);
        if(packageName==null)
        {
            log.error(posToken,"package缺少名称");
            return null ;
        }
        else {
            /* 创建PACKAGE语法树实例 */
            JCPackage pd = treeMaker.at(posToken).PackageDecl(packageName);
            return pd;
        }
    }

    /** 分析限定名称,
    * Fullname ::=   Ident { "." Ident }
     */
    JCFullname parseFullName( ) {
        JCFullname jcFullName = new JCFullname();
        Token nameToken;

        /* 读取开头的标识符 */
        nameToken = parseNameToken();
        jcFullName.add(nameToken);
        /* 判断是否一直是点号 */
        while (kind == DOT) {
            nextToken();
            nameToken = parseNameToken();
            if(nameToken==null)
                break;
            jcFullName.add(nameToken);
        }
        return jcFullName;
    }

    /** 分析导入语句 import ...;
     * Import = 'import' Fullname ";"
     * */
    JCImport parseImport() {
        /* 记录开始词法标记 */
        Token posToken = token;
        /* 验证关键字 */
        accept(IMPORT);
        JCFullname typeNameTree = parseFullName();
        /* /验证末尾是否是分号 */
        accept(TokenKind.SEMI);
        return treeMaker.at(posToken).Import(typeNameTree);
    }

    /** 分析定义函数语法
     *  Method ::= Expression Type '(' Parameters ')' Block
     * */
    JCMethod parseMethod( ) {
        /* 记录开始token */
        Token posToken = token;
        /* 分析返回类型 */
        JCExpression retTypeExpr = parseType();
        /* 分析函数名称 */
        Token nameToken =  parseNameToken();
        /* 分析参数列表 */
        ArrayList<JCVariableDecl> params = parseMethodParameters();
        /* 分析函数体 */
        JCBlock body = block();
        /* 生成定义函数语法树 */
        return treeMaker.at(posToken).MethodDef( retTypeExpr,nameToken, params, body);
    }

    /** 分析函数参数列表
     * '(' Parameters ')'
     * */
    ArrayList<JCVariableDecl> parseMethodParameters( ) {
        ArrayList<JCVariableDecl> params = new ArrayList<>();
        accept(LPAREN); //接收左括号
        if ( kind != RPAREN) //当前不是右括号，表示参数列表还未结束
        {
            /* 1:分析第一个参数 */
            addParsedParameter(params);
            /*  2：如果当前是逗号,则跳过这个逗号,继续分析参数知道为否*/
            while ( kind == COMMA) {
                accept(COMMA);  //接收逗号
                addParsedParameter(params);
            }
        }
        accept(RPAREN); //接收右括号
        return params;
    }

    /** 分析函数声明的单个参数,并检查添加到列表  */
    void addParsedParameter(ArrayList<JCVariableDecl> params)
    {
        JCVariableDecl param = parseParameter();
        /* 检查参数是否正确,正确的才添加 */
        if (param!=null &&param.varType!=null &&  param.nameExpr != null)
            params.add(param);
    }

    /** 分析函数声明的单个参数
     *  FormalParameter ::= Type name
     */
    JCVariableDecl parseParameter() {
        Token posToken = token;
        /* 分析类型 */
        JCExpression type = parseType();
        if( type==null )
            return null;
        /* 分析参数名称 */
        JCIdent name = parseIdent();
        if(name==null )
            return null;
        return treeMaker.at(posToken).VarDef(type, name, null,false);
    }

    /* 分析标识符 */
    JCIdent parseIdent()
    {
        /* 记录开始位置词法标记 */
        Token posToken =  token;
        if (kind == IDENTIFIER) {
            nextToken();
            /* 用posToken创建标识符表达式 */
            JCIdent jcIdent = treeMaker.at(posToken).Ident(posToken);
            return jcIdent;
        }
        else
        {
            /* 错误处理 */
            log.error(posToken,"期望标识符");
            return null;
        }
    }

    /** 分析语句 ,从简单到复杂排列*/
    JCStatement parseStatement()
    {
        switch (kind) {
            case LBRACE:
                return block();//分析语句块
            case RETURN:
                return parseReturn();
            case WHILE:
                return parseWhile();
            case IF:
                return parseIf();
            //case FOR:
            //    return parseFor();
            case INT:
            case BOOLEAN:
            case IDENTIFIER:
                return expStmt();//分析表达式语句
            case SEMI:
                nextToken(); //跳过无意义的分号
                return parseStatement();
            case ELSE:
                log.error( token,"ELSE缺少IF" );
                nextToken();
                return parseStatement();
            case RBRACE:
                log.error( token,"右大括号没有匹配的花大括号" );
                nextToken();
                return parseStatement();
            case EOF:
                return null;
            default:
                /* 错误处理 */
                log.error( token,"非法的表达式语句成分" );
                nextToken();
                return parseStatement();
        }
    }

    /**
     * 分析语句块
     * Block ::= "{" Statements "}"
     */
    JCBlock block( ) {
        /* 记录开始token */
        Token posToken = token;
        /* 验证左大括号*/
        accept(LBRACE);
        /* 用于保存分析出的语句 */
        ArrayList<JCStatement> stats= new ArrayList<>();
        while (true)
        {
            /*  当前符号是终止符'}',跳出循环 */
            if(kind== RBRACE)
                break;
            /* 当前符号是件末尾,跳出循环 */
            if(kind== EOF)
                break;
            /* 分析语句,不为null保存 */
            JCStatement statement = parseStatement();
            if(statement!=null)
                stats.add(statement);
        }
        /* 验证右大括号*/
        accept(RBRACE);
        /* 生语句块语法树 */
        return treeMaker.at(posToken).Block(stats);
    }

    /** 分析if语句
     * IF ::= 'if' '(' Expression ')' Statement [ 'else' Statement]
     * */
    JCIf parseIf()
    {
        /* 记录开始token */
        Token posToken = token;
        /* 验证关键字IF */
        accept(IF);
        /* 分析括号表达式 */
        /* 验证左括号 */
        accept(LPAREN);
        /* 分析括号中的表达式 */
        JCExpression cond = parseExpression();
        /* 验证右括号 */
        accept(RPAREN);
        /* 分析循环体 */
        /* 分析THEN部分 */
        JCStatement thenPart = parseStatement();
        /* 分析ELSE部分 */
        JCStatement elsePart = null;
        /* ELSE部分是可选的,所以要用IF判断一下 */
        if (kind == ELSE) {
            accept(ELSE);
            elsePart = parseStatement();
        }
        /* 生成IF语法树 */
        return treeMaker.at(posToken).If(cond, thenPart, elsePart);
    }

    /** 分析while循环
     * While ::= 'while' '(' Expr ')' Statement
     * */
    JCWhile parseWhile()
    {
        /* 记录开始token */
        Token posToken = token;
        /* 验证关键字WHILE */
        accept(WHILE);
        /* 分析括号表达式 */
        /* 验证左括号 */
        accept(LPAREN);
        /* 分析括号中的表达式 */
        JCExpression cond = parseExpression();
        /* 验证右括号 */
        accept(RPAREN);
        /* 分析循环体 */
        JCStatement body = parseStatement();
        /* 生成WHILE语法树 */
        return treeMaker.at(posToken).WhileLoop(cond, body);
    }

    /** 分析return语句
     * Return ::='return' [Expression] ";"
     * */
    JCReturn parseReturn()
    {
        /* 记录开始token */
        Token posToken = token;
        /* 验证关键字 RETURN */
        accept(RETURN);
        /* RETURN 后的表达式是可选的 */
        JCExpression expr = null;
        /* 判断当前是否是分号,如果不是，读取表达式 */
        if(kind != SEMI)
            expr = parseExpression();
        /* 验证语句末尾分号 */
        accept(SEMI);
        /* 生成语法树实例 */
        return treeMaker.at(posToken).Return(expr);
    }

    /** 分析表达式语句
     * ExpressionStatement ::= expression ExpStmtOther
     * */
    JCExprStatement expStmt(  )
    {
        Token posToken = token;
        /** 分析开头表达式 */
        JCExpression exp  = parseExpression();
        return expStmtOther(exp,posToken);
    }

    /** 分析表达式语句后面部分
       ExpStmtOther ::=
         | EXP '=' EXP ';'
         | EXP Ident ';'
         | EXP ';'
     * */
    JCExprStatement expStmtOther(JCExpression exp,Token posToken)
    {
        switch (kind)
        {
            case EQ:
                return assignExpStmt(exp,posToken); //赋值表达式
            case IDENTIFIER:
                return varDeclExpStmt(exp,posToken); // 变量声明表达式
            case SEMI:
                return normalExpStmt(exp,posToken); //一般表达式语句
            default:
                error(posToken, "不是正确的表达式语句");
                return null;
        }
    }

    /** 分析赋值表达式语句
      Assign ::= expression '=' expression
     */
    JCExprStatement assignExpStmt(JCExpression exp,Token posToken)
    {
        accept(EQ); //验证赋值号
        JCExpression right = parseExpression() ; //分析右表达式
        exp = treeMaker.at(posToken).Assign(exp, right); //创建 JCAssign
        accept(SEMI);
        return treeMaker.at(posToken).Exec(exp);
    }

    /** 分析变量声明表达式语句 */
    JCExprStatement varDeclExpStmt(JCExpression exp,Token posToken)
    {
        exp = variableDecl(exp,posToken);
        accept(SEMI);
        return treeMaker.at(posToken).Exec(exp);
    }

    /** 分析普通表达式语句 */
    JCExprStatement normalExpStmt(JCExpression exp,Token posToken)
    {
        accept(SEMI);
        return treeMaker.at(posToken).Exec(exp);
    }

    /**
     *  Expression ::= ExpressionrAndOr
     */
    JCExpression parseExpression() {
        JCExpression exp = parseExprAndOr();
        return exp;
    }

    /** 分析与或逻辑表达式
     *  ExpressionrAndOr ::= ExpressionrCompare [ AndOrOp ExpressionrCompare ]
     *  AndOrOp ::= '&&' | '||'
     * */
    JCExpression parseExprAndOr()
    {
        /* 记录位置Token */
        Token posToken =  token;
        /* 分析开头的表达式,按运算符优先级,调用分析比较表达式 */
        JCExpression tempExpr = parseExprCompare();
        /* 循环当前符号是与或运算符时 */
        while(kind == TokenKind.AND || kind == TokenKind.OR)
        {
            /* 记录当前运算符并移到下一词法标记 */
            TokenKind op= kind;
            nextToken();
            /* 分析运算符右边的表达式 */
            JCExpression rightExpr = parseExprCompare();
            /* 用左右两个表达式和运算符生成表达式语法树,并赋值到temp */
            tempExpr = treeMaker.at(posToken).Binary(op, tempExpr , rightExpr);
        }
        return tempExpr;
    }

    /** 分析比较表达式
     *  ExpressionrCompare ::= ExpressionrAddSub [ CompareOp ExpressionrAddSub ]
     *  CompareOp ::= '>' | '<' | '==' | '>='  | '<='  | '!='
     * */
    JCExpression parseExprCompare()
    {
        Token posToken = token;
        JCExpression tempExpr = parseExprAddSub();
        while(kind == EQEQ || kind == NOTEQ || kind == LT
                || kind == GT|| kind == LTEQ|| kind == GTEQ)
        {
            TokenKind op= kind;
            nextToken();
            JCExpression rightExpr = parseExprAddSub();
            tempExpr = treeMaker.at(posToken).Binary(op, tempExpr , rightExpr);
        }
        return tempExpr;
    }

    /** 分析加减表达式
     *  ExpressionrAddSub ::= ExpressionrMulDiv [ CompareAddSub  ExpressionrMulDiv ]
     *  CompareAddSub ::= '+' | '-'
     * */
    protected JCExpression parseExprAddSub()
    {
        Token posToken =  token;
        JCExpression tempExpr = parseExprMulDiv();
        while(kind == TokenKind.ADD || kind == TokenKind.SUB)
        {
            TokenKind op= kind;
            nextToken();
            JCExpression rightExpr = parseExprMulDiv();
            tempExpr = treeMaker.at(posToken).Binary(op, tempExpr , rightExpr);
        }
        return tempExpr;
    }

    /** 分析乘除表达式
     *  ExpressionrMulDiv ::= Unary [ CompareMulDiv  Unary ]
     *  CompareMulDiv ::= '*' | '/'
     * */
    JCExpression parseExprMulDiv()
    {
        Token posToken =  token;
        JCExpression expression = parseExprNot();
        while(kind == TokenKind.MUL || kind == TokenKind.DIV)
        {
            TokenKind op= kind;
            nextToken();
            JCExpression rightExpr = parseExprNot();
            expression = treeMaker.at(posToken).Binary(op, expression , rightExpr);
        }
        return expression;
    }

    /** 分析逻辑非表达式
     *  NotExpr ::= '!' PrimaryExpr
     * */
    JCExpression parseExprNot()
    {
        if(kind == TokenKind.NOT)
        {
            Token posToken =  token;
            nextToken();
            /* 分析右部表达式*/
            JCExpression rightExpr = primaryExpr();
            return treeMaker.at(posToken).Unary(TokenKind.NOT, rightExpr);
        }
        else
        {
            return primaryExpr();
        }
    }

    /** 分析成员访问或函数调用表达式
     * PrimaryExpr ::= FieldAccess | MethodInvocation
     * */
    JCExpression primaryExpr()
    {
        Token posToken = token;
        /* 分析开头 */
        JCExpression tempExpr = termExpr();
        while (kind !=EOF)
        {
            switch (kind) {
                case DOT:
                    tempExpr = parseFieldAccess(tempExpr,posToken);
                    break;
                case LPAREN:
                    tempExpr = methodInvocation(tempExpr,posToken);
                    break;
                default:
                    return tempExpr;
            }
        }
        return tempExpr;
    }

    /** 分析成员访问表达式
     * FieldAccess ::= TermExpr '.' Ident
     * */
    JCExpression parseFieldAccess(JCExpression expr, Token posToken) {
        accept(DOT);
        Token nameToken = parseNameToken();
        if(nameToken!=null)
            expr = treeMaker.at(posToken).FieldAccess(expr, nameToken);
        return expr;
    }

    /** 分析函数调用表达式
     *     MethodInvocation ::= TermExpr '(' Arguments ')'
     * */
    JCMethodInvocation methodInvocation(JCExpression t, Token posToken) {
        ArrayList<JCExpression> args = methodInvocationArguments();
        JCMethodInvocation mi = treeMaker.at(posToken).Apply( t, args);
        return mi;
    }

    /** 分析函数参数
     *  Arguments = "(" [Expression [ ',' Expression ]] ")"
     */
    ArrayList<JCExpression> methodInvocationArguments() {
        ArrayList<JCExpression> args = new ArrayList<>();
        accept(LPAREN);
        if (kind != RPAREN) {
            JCExpression argExpr = parseExpression();
            if (argExpr != null)
                args.add(argExpr);
            while (kind == COMMA) {
                Token commaToken = token;
                nextToken();
                argExpr = parseExpression();
                if (argExpr != null)
                    args.add(argExpr);
                else
                    error(commaToken,"多余的符号");
            }
        }
        accept(RPAREN);
        return args;
    }

    /** 分析基本元素表达式
     * Term ::= Literal | ParExpression | PrimitiveType | NewExpression | Ident
     * */
    JCExpression termExpr() {
        switch (kind) {
            case INTLITERAL:
            case STRINGLITERAL:
            case TRUE:
            case FALSE:
                return literal();
            case LPAREN:
                return parens();
            //case NEW:
            //    return parseNew();
            case VOID:
            case INT:
            case BOOLEAN:
                return primitiveType();
            case IDENTIFIER:
                return parseIdent();
            default:
                return null;
        }
    }

    /** 分析字面值常量表达式
     * Literal =
     *   | INTLITERAL
     *   | STRINGLITERAL
     *   | TRUE
     *   | FALSE
     */
    JCExpression literal( ) {
        Object value = parseTokenValue();
        JCLiteral literal = treeMaker.at(token).Literal(value);
        nextToken();
        return literal;
    }

    /* 根据词法标记类型取对应的值 */
    private Object parseTokenValue()
    {
        switch (kind) {
            case STRINGLITERAL:
                return token.valueString;
            case TRUE:
                return true;
            case FALSE:
                return false;
            case INTLITERAL: {
                String valStr = token.valueString;
                try {
                    return Integer.parseInt(valStr);
                } catch (NumberFormatException ex) {
                    log.error(token,"整数数值错误或者过大");
                    return 0;
                }
            }
            default:
                throw new CompileError();
        }
    }

    /** 分析括号表达式
     * ParExpression = "(" Expression ")"
     */
    JCParens parens() {
        Token posToken = token;
        /* 验证左括号 */
        accept(LPAREN);
        /* 分析括号中的表达式 */
        JCExpression t = parseExpression();
        /* 验证右括号 */
        accept(RPAREN);
        /* 生成语法树并返回 */
        return treeMaker.at(posToken).Parens(t);
    }

    /** 分析new表达式
     * NewExpression ::= 'new' Ident '(' [Args] ')'
     * */
   // JCExpression parseNew() {
        /* 记录开始token */
     //   Token posToken = token;
        /* 验证 NEW 关键字 */
    //    accept(NEW);
    //    JCIdent classExpr = parseIdent();
    //    if (kind == LPAREN) {
    //        ArrayList<JCExpression> args = methodInvocationArguments();
    //        return treeMaker.at(posToken).NewClass(classExpr, args);
     //   }
     //   else {
     //       error(posToken,"需要 '(' ");
     //       ArrayList<JCExpression> args =  new ArrayList<>();
     //       return treeMaker.at(posToken).NewClass(classExpr, args);
    //    }
    //}

    /** 分析类型
     *  Type ::= 'void' | 'int' | 'boolean' | Ident
     * */
    JCExpression parseType()
    {
        Token posToken = token;
        JCExpression expr;
        switch (kind) {
            case VOID:
            case INT:
            case BOOLEAN:
                expr = primitiveType();
                break;
            case IDENTIFIER:
                return parseIdent();
            default:
                error(posToken,"非法的类型");
                nextToken();
                return null;
        }
        return expr;
    }

    /** 分析基本数据类型表达式
     * PrimitiveType  ::=  'void' | 'int' | 'boolean'
     */
    JCPrimitiveType primitiveType() {
        /* 根据当前的词法类型的标记创建语法树 */
        JCPrimitiveType tree = treeMaker.at(token).PrimitiveType(kind);
        /* 必须移过处理过的词法标记 */
        nextToken();
        return tree;
    }

    /** 分析变量声明
     * VariableDecl ::= Type  Ident [ '=' Expression]
     * */
    JCVariableDecl variableDecl(JCExpression type,Token posToken) {
        JCIdent name = parseIdent();
        if(!(type instanceof JCIdent
                ||type instanceof JCFieldAccess
                || type instanceof JCPrimitiveType))
        {
            error(posToken,"表达式不能作为类型");
        }

        JCExpression init = null;
        if (kind == TokenKind.EQ) //如果是'=',变量有初始值
        {
            nextToken();
            init = parseExpression();
        }
        JCVariableDecl result = treeMaker.at(posToken)
                .VarDef(type,name, init,true);
        return result;
    }

    Token parseNameToken() {
        if (kind == IDENTIFIER) {
            Token tmpToken = token;
            nextToken();
            return tmpToken;
        }
        else {
            error(token,"不是标识符" );
            return null;
        }
    }
}
