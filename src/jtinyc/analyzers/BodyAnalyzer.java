/*
 * Jtiny compiler source code
 * License : the GNU General Public License
 * Copyright (C) 2021-2024 Chines JSSQSY WANG
 */
package jtinyc.analyzers;
import jtinyc.lex.*;
import jtinyc.utils.*;
import jtinyc.trees.*;
import jtinyc.symbols.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/** 函数体语义分析器 */
public class BodyAnalyzer extends TreeScanner<BodyContext>
{
    public void visitMethods(JCFile tree) {
        for(JCMethod methodDecl:tree.methods)
            methodDecl.scan(this,null);
    }

    private void visit(JCTree tree, BodyContext arg)
    {
        if (tree != null)
            tree.scan(this,arg);
    }

    /** 分析函数体 */
    @Override
    public void visitMethodDef(JCMethod tree, BodyContext arg)
    {
        arg = new BodyContext();
        /* 放入当前函数符号 */
        arg.methodSymbol =  tree.methodSymbol;
        /* 放入当前函数作用域 */
        arg.scope = tree.scope;
        /* 分析函数体 */
        visit(tree.body,arg);
    }

    /** 分析while语义 */
    @Override
    public void visitWhileLoop(JCWhile tree, BodyContext arg)
    {
        /*分析条件表达式*/
        visit(tree.cond,arg);
        /*检查条件表达式的结果是不是boolean类型*/
        AnalyzerUtil.checkBoolean(tree.cond);
        /* 分析循环体 */
        visit(tree.body,arg);
    }

    /** 分析if选择语句 */
    @Override
    public void visitIf(JCIf tree, BodyContext arg)
    {
        /*分析条件表达式*/
        visit(tree.cond,arg);
        AnalyzerUtil.checkBoolean(tree.cond);

        visit(tree.thenpart,arg); //分析true语句
        visit(tree.elsepart,arg); //分析false语句
    }

    /** 分析for语义 */
    //@Override
    //public void visitForLoop(JCForLoop tree, BodyContext arg)
    //{
        /* 创建一个新的作用域 */
      //  BodyContext newContext = arg.newScope();
      //  tree.scope = newContext.scope; //保存这个frame,生成阶段需要
      //  visit(tree.init,newContext); //分析初始化语句
    //    visit(tree.cond,newContext); //分析循环条件表达式
     //   visit(tree.step,newContext); //分析更新语句
     //   visit(tree.body,newContext); //分析循环体
        //检查条件表达式的结果是不是boolean类型
     //   AnalyzerUtil.checkBoolean(tree.cond);
   // }

    /** 分析语句块语义 */
    @Override
    public void visitBlock(JCBlock tree,  BodyContext arg)
    {
        //BodyContext context = tree.isForLoopBody ? arg : arg.newScope();
        /* 创建子作用域 */
        BodyContext context = arg.newScope();
        /* 保存这个scope,生成阶段需要 */
        tree.scope = context.scope;
        /* 分析块内语句 */
        for(JCTree stmt:tree.statements)
            visit(stmt,context);
    }

    /** 分析return语义 */
    @Override
    public void visitReturn(JCReturn tree, BodyContext arg)
    {
        /*分析返回表达式 */
        visit(tree.expr,arg);
        /* 取出函数的返回类型 */
        BTypeSymbol returnTypeSymbol = arg.methodSymbol.returnType;
        if(SymbolUtil.isVoid(returnTypeSymbol))
        {
            if( tree.expr!=null) {
                tree.expr.error("不需要返回值");
            }
        }
        else
        {
            if(tree.expr==null)
            {
                tree.expr.error("缺少返回值" );
            }
            else
            {
                /* 分析返回值类型是否与函数定义的返回类型兼容 */
                BTypeSymbol rightType = tree.expr.symbol.getTypeSymbol();
                if(!AnalyzerUtil.checkAssignable(returnTypeSymbol,rightType))
                    tree.expr.error("不兼容的返回类型");
            }
        }
    }

    /** 分析表达式语句语义 */
    @Override
    public void visitExprStmt(JCExprStatement tree, BodyContext arg)
    {
        visit(tree.expr,arg);
    }

    /** 分析括号表达式语义 */
    @Override
    public void visitParens(JCParens tree, BodyContext arg)
    {
        visit(tree.expr,arg);
        tree.symbol = tree.expr.symbol;
    }

    /** 分析基本类型语义 */
    @Override
    public void visitPrimitiveType(JCPrimitiveType tree, BodyContext arg) {
        tree.symbol= AnalyzerUtil.getPrimitiveSymbol(tree.kind);
    }

    /** 分析常量值语义 */
    @Override
    public void visitLiteral(JCLiteral tree, BodyContext arg) {
        tree.symbol= AnalyzerUtil.getSymbolByLiteralKind(tree.value);
    }

    /** 分析数组类型语义 */
   /* @Override
    public void visitArrayType(JCArrayTypeTree tree, BodyContext arg)
    {
        BTypeSymbol elemTypeSymbol =SearchSymbol.findType(tree.elemType,arg.scope); //查找类型
        BArrayTypeSymbol arrayTypeSymbol = BArrayTypeSymbol.getSymbol(elemTypeSymbol); //创建数组类型符号
        tree.symbol =arrayTypeSymbol; //保存符号
    }*/

    /** 分析赋值表达式 */
    @Override
    public void visitAssign(JCAssign tree, BodyContext arg)
    {
        tree.symbol = RClassSymbolManager.voidPrimitiveSymbol;
        /* 分析左右表达式 */
        visit(tree.left,arg);
        visit(tree.right,arg);
        /* 检查左右是否可以赋值 */
        BTypeSymbol leftType = tree.left.symbol.getTypeSymbol();
        BTypeSymbol rightType = tree.right.symbol.getTypeSymbol();
        boolean assignable = AnalyzerUtil.checkAssignable(leftType,rightType);
        if(!assignable)
        {
            tree.right.error("类型无法赋值");
        }

        /* 检查左部表达式是否可写 */
        if(!AnalyzerUtil.writable(tree.left))
            tree.left.error("表达式'%s'是只读的,不能被修改",tree.left.toString());
    }

    /** 分析标识符语义 */
    @Override
    public void visitIdent(JCIdent tree, BodyContext arg) {
        String varName = tree.getName();
        /* 在当前作用域根据searchKinds搜索变量*/
        ArrayList<Symbol> symbols = SymbolSearcher.findIdents(arg.scope, varName);
        if (symbols.size() == 0) {
            /* 没有找到对应符号，报错 */
            tree.error(tree.nameToken, "找不到变量'%s'", varName);
            tree.symbol = new BErroneousSymbol();
        } else if (symbols.size() == 1) {
            Symbol symbol = symbols.get(0);
            tree.symbol = symbol;
        } else {
            /* 找到多个符号，无法明确具体是哪个符号，创建BManySymbol返回，让其它地方处理 */
            tree.error( tree.nameToken , "有歧义的多个符号'%s'", varName);
            tree.symbol = new BErroneousSymbol();
        }
    }

    /** 分析访问数组表达式 */
  /*  @Override
    public void visitArrayAccess(JCArrayAccess tree, BodyContext arg)
    {*/
        /** 创建新的BodyContext，只搜索变量 */
       /* BodyContext context = arg.copy(new SearchKinds());
        visit(tree.indexed,context); //分析数组变量表达式
        visit(tree.index,context);    // 分析数组索引表达式*/
        /* 检查索引是不是int类型 */
      /*  if(!(AnalyzerUtil.isInt(tree.index)))
            tree.index.error("数组索引无法转换为int");*/
        /** 检测被访问变量是否是数组类型 */
      /*  BTypeSymbol indexedType = tree.indexed.symbol.getTypeSymbol();
        if(indexedType  instanceof BArrayTypeSymbol)
        {
            BArrayTypeSymbol arrayTypeSymbol = (BArrayTypeSymbol) indexedType ;
            tree.symbol= arrayTypeSymbol.elementType;
        }
        else
        {
            tree.indexed.error("不是数组类型");
            tree.symbol= indexedType;
        }
    }*/

    /** 分析一元运算表达式 */
    @Override
    public void visitUnary(JCUnary tree, BodyContext arg)
    {
        visit(tree.expr,arg);

        TokenKind opcode= tree.opcode;
        if(opcode== TokenKind.NOT) {
            tree.symbol = RClassSymbolManager.booleanPrimitiveSymbol;
            /* 检查条件表达式的结果是不是boolean类型 */
            if(!AnalyzerUtil.isBoolean(tree.expr))
            {
                tree.expr.error("不兼容的类型,无法转换为boolean");
            }
        }
        else if(opcode== TokenKind.SUB ||opcode== TokenKind.ADD) {
            tree.symbol = RClassSymbolManager.intPrimitiveSymbol;
            /* 检查条件表达式的结果是不是int类型 */
            if(!AnalyzerUtil.isInt(tree.expr))
                tree.expr.error("不兼容的类型,无法转换为int");
        }
        else
        {
            tree.expr.error("运算符左边缺少表达式");
            tree.symbol =  tree.expr.symbol.getTypeSymbol();
        }
    }

    /** 分析二元运算表达式 */
    @Override
    public void visitBinary(JCBinary tree, BodyContext arg)
    {
        /** 先分析左右两边表达式*/
        visit(tree.left,arg);
        visit(tree.right,arg);
        BTypeSymbol leftType = tree.left.symbol.getTypeSymbol();
        BTypeSymbol rightType = tree.right.symbol.getTypeSymbol();
        switch (tree.opcode)
        {
            case AND: case OR:
                tree.symbol=RClassSymbolManager.booleanPrimitiveSymbol;
                AnalyzerUtil.checkBoolean(tree.left);
                AnalyzerUtil.checkBoolean(tree.right);
                break;
            case EQEQ:case NOTEQ:
                tree.symbol=RClassSymbolManager.booleanPrimitiveSymbol;
                if(SymbolUtil.isBoolean(leftType)||SymbolUtil.isBoolean(rightType))
                {

                }
                else if(SymbolUtil.isInt(leftType)||SymbolUtil.isInt(rightType))
                {

                }
                else
                {
                    tree.error("只有int或boolean之间才可以比较等于不等于");
                }
                break;
            case GT:case GTEQ: case LTEQ: case LT:
                tree.symbol=RClassSymbolManager.booleanPrimitiveSymbol;
                AnalyzerUtil.checkNumber(tree.left);
                AnalyzerUtil.checkNumber(tree.right);
                break;
            case ADD:
                tree.isStringContact =
                        (SymbolUtil.isString(leftType)||SymbolUtil.isString(rightType));
                if(tree.isStringContact)
                    tree.symbol=RClassSymbolManager.StringSymbol;
                else
                {
                    tree.symbol=RClassSymbolManager.intPrimitiveSymbol;
                    AnalyzerUtil.checkNumber(tree.left);
                    AnalyzerUtil.checkNumber(tree.right);
                }
                break;
            case SUB: case MUL: case DIV:
                tree.symbol=RClassSymbolManager.intPrimitiveSymbol;
                AnalyzerUtil.checkNumber(tree.left);
                AnalyzerUtil.checkNumber(tree.right);
                break;
            default:
                throw new CompileError();
        }
    }

    /** 分析变量声明语义 */
    @Override
    public void visitVarDef(JCVariableDecl tree, BodyContext arg) {
        Token nameToken = tree.nameExpr.nameToken;
        /* 查找这个变量名称是否已经使用过 */
        String varName = tree.nameExpr.getName();
        List<Symbol> symbols = SymbolSearcher.findIdents(arg.scope, varName);
        BVarSymbol varSymbol;
        if(symbols.size()==0)
        {
            /* 查找变量声明类型部分 */
            BTypeSymbol typeSymbol = SymbolSearcher.findType(tree.varType, arg.scope);
            tree.varType.symbol = typeSymbol;
            /* 创建新变量符号，并加入的当前作用域中 */
            varSymbol = new DVarSymbol( varName, VarSymbolKind.localVar,typeSymbol);
            arg.scope.addSymbol(varSymbol);
        }
        else
        {
            tree.error(nameToken,"已经定义了变量 '%s'", varName);
            /* 把变量符号设为搜索到的符号，以方便后续语义分析 */
            varSymbol = (BVarSymbol) symbols.get(0);
        }

        tree.nameExpr.symbol = varSymbol;

        /* 检查赋值类型 */
        if (tree.init != null)
        {
            /* 分析初始值表达式 */
            visit(tree.init,arg);
            boolean assignable = AnalyzerUtil.checkAssignable
                    (varSymbol.getTypeSymbol(),tree.init.symbol.getTypeSymbol());
            if(!assignable)
                tree.nameExpr.error( nameToken, "变量'%s'无法赋值",nameToken.identName );
        }
    }
/*
    private void attrType(JCExpression typeExpr,SymbolScope scope)
    {
        if(typeExpr.symbol!=null)
            return ;

        if (typeExpr instanceof JCPrimitiveType) {
            JCPrimitiveType primitiveType = (JCPrimitiveType)typeExpr;
            typeExpr.symbol =SymbolSearcher.getPrimitiveSymbol(primitiveType.kind);
        }
        //else if (typeExpr instanceof JCFieldAccess) {
        //    return findType( (JCFieldAccess)exp);
        //}
        else if (typeExpr instanceof JCIdent) {
            attrType( (JCIdent)typeExpr,scope);
        }
        /*else if (exp instanceof JCArrayTypeTree) {
            return findType( (JCArrayTypeTree)exp,scope);
        }*/
        /*else {
            throw new CompileError();
        }
    }*/

    /** 分析点运算符表达式 */
    @Override
    public void visitFieldAccess(JCFieldAccess tree , BodyContext arg)
    {
        /* 分析点运算的前表达式 */
        visit(tree.selected,arg);
        Symbol selectedSymbol = tree.selected.symbol;

        boolean isStatic = false;
        if(selectedSymbol instanceof BTypeSymbol)
        {
            /* 1: 类型名称访问成员,表面它是静态成员 */
            isStatic = true;
        }
        else if(selectedSymbol instanceof BVarSymbol)
        {
            /* 2: 被选择表达式是变量,查找它的实例成员 */
            isStatic = false;
        }
        else if(selectedSymbol instanceof BManySymbol)
        {
            Token nameToken = tree.nameToken;
            /* 3: 类型是BManySymbol,说明有多个类型，无法唯一确定，导致程序有歧义 */
            tree.error( nameToken , "有歧义的多个符号'%s'", nameToken.identName);
            tree.symbol= new BErroneousSymbol();
            return;
        }
        else if(selectedSymbol instanceof BErroneousSymbol)
        {
            tree.symbol= selectedSymbol;
            return;
        }
        else {
            throw new CompileError();
        }

        /* 分析成员 */
        tree.symbol = attrMemberSymbol(tree,isStatic );
    }

    /* 分析类型成员 */
    private static Symbol attrMemberSymbol(JCFieldAccess tree, boolean isStatic )
    {
        BTypeSymbol typeSymbol = tree.selected.symbol.getTypeSymbol();
        Token nameToken = tree.nameToken;
        String memberName = nameToken.identName;
        ArrayList<Symbol> symbols = typeSymbol.findMembers(memberName);
        if(symbols==null || symbols.size()==0)
        {
            /* 没有找到成员报错 */
            tree.error(nameToken, "找不到符号'%s'",memberName);
            return new BErroneousSymbol();
        }
        else if(symbols.size()==1)
        {
            Symbol symbol = symbols.get(0);
            /* 只找到一个成员时,检查static修饰符是否相符 */
            if(SymbolUtil.isStatic(symbol)!=isStatic)
                tree.error(nameToken, "'%s'静态修饰符不同", memberName);
            return symbol;
        }
        else
        {
            /* 找到多个成员，一般都是重载的函数，按isStatic进行筛选 */
            ArrayList<Symbol> symbols2 = new ArrayList<>();
            for(Symbol symbol :symbols)
            {
                if(SymbolUtil.isStatic(symbol)==isStatic)
                    symbols2.add(symbol);
            }

            if(symbols2.size()==0)
            {
                tree.error(nameToken, "'%s'静态修饰符不同",memberName);
                return new BErroneousSymbol();
            }
            else if(symbols2.size()==1)
            {
                return symbols2.get(0);
            }
            else
            {
                /* 筛选后还有多个,把这些符号合并到BManySymbol中,让下一步分析进行筛选 */
                BManySymbol moreSym = new BManySymbol(memberName,symbols2);
                return moreSym;
            }
        }
    }

    /** 分析创建数组实例 */
 /*   @Override
    public void visitNewArray(JCNewArray tree, BodyContext arg) {*/
        /* 分析数组类型 */
      /*  BTypeSymbol elemTypeSymbol =SearchSymbol.findType(tree.elemtype, arg.scope);
        visit(tree.lengthExpr, arg);*/
        /* 检查条件表达式的结果是不是int类型 */
    /*    if (!(AnalyzerUtil.isInt(tree.lengthExpr)))
            tree.lengthExpr.error(" '%s'是不兼容的类型,无法转换为 int", tree.lengthExpr.toString());
        BArrayTypeSymbol arrayTypeSymbol = BArrayTypeSymbol.getSymbol(elemTypeSymbol);
        tree.elemtype.symbol = elemTypeSymbol;
        tree.symbol = arrayTypeSymbol;
    }*/

    /** 分析new表达式 */
    // @Override
    //  public void visitNewClass(JCNewClass tree, BodyContext arg)
    // {
        /*分析new类型 */
        //   attrType(tree.clazzExpr,arg.scope);
        //  BTypeSymbol typeSymbol = tree.clazzExpr.symbol.getTypeSymbol();
        /* new表达式的结果类型就是声明的变量类型 */
        //  tree.symbol= tree.clazzExpr.symbol;

        /* 分析参数 */
        //  ArrayList<BTypeSymbol> argTypes = attrArgs(tree.args,arg);

        // if(argTypes==null && !(typeSymbol instanceof BErroneousSymbol)) {
            /* 根据这些参数类型符号查找构造函数 */
            //      ArrayList<BMethodSymbol> methodSymbols = SymbolUtil.findConstructor(typeSymbol, argTypes);
            //      if (methodSymbols == null || methodSymbols.size() == 0)
                //          tree.error("无法将构造器应用到给定类型");
            //      else if (methodSymbols.size() == 1)
                //          tree.constructorSymbol = methodSymbols.get(0);
            //       else
                //           tree.error("构造器不明确");
            //   }
    //}

    private void attrType(JCIdent typeExpr,SymbolScope scope)
    {
        String typeName = typeExpr.getName();
        List<Symbol> typeSymbols = SymbolSearcher.findIdents(scope, typeName)
                .stream().filter(A->A instanceof BTypeSymbol).collect(Collectors.toList());
        if (typeSymbols.size() == 1) {
            typeExpr.symbol= typeSymbols.get(0);
        }
        else if (typeSymbols.size() == 0) {
            typeExpr.symbol= new BErroneousSymbol();
        }
        else {
            typeExpr.error(String.format("对'%s'的引用不明确",typeName));
            typeExpr.symbol= new BErroneousSymbol();
        }
    }

    /**分析参数并且返回这些参数的类型符号列表  */
    private ArrayList<BTypeSymbol> attrArgs(ArrayList<JCExpression> args, BodyContext context)
    {
        ArrayList<BTypeSymbol> argTypes = new ArrayList<>();
        boolean argError = false; // 参数中是否有错误
        for (JCExpression item : args)
        {
            /* 分析新的表达式，解除搜索标识符限制 */
            item.scan(this, context);
            /* 某个参数未分析正确 */
            if( item.symbol instanceof BErroneousSymbol)
            {
                argError = true;
            }
            else if(item.symbol instanceof BManySymbol)
            {
                item.error("有歧义的多个符号'%s'", item.toString());
                argError = true;
            }
            //if(item==null || item.symbol==null || item.symbol.getTypeSymbol()==null)
            //{
            //    Debuger.outln("attrArgs null");
                //item.scan(this, argContext);
            //}
            BTypeSymbol argTypeSymbol = item.symbol.getTypeSymbol();
            if(SymbolUtil.isVoid(argTypeSymbol))
            {
                item.error("没有返回值'%s'", item.toString());
                argError = true;
            }
            argTypes.add(argTypeSymbol);
        }
        if(argError)
            return null;
        else
            return argTypes;
    }

    /** 分析函数调用 */
    @Override
    public void visitMethodInvocation(JCMethodInvocation tree, BodyContext arg) {
        /* 分析方法名称,只搜索方法 */
        visit(tree.methodExpr, arg);
        /* 分析参数,获取参数类型 */
        ArrayList<BTypeSymbol> argTypes = attrArgs(tree.args,arg);
        if(argTypes==null ){
            tree.symbol = new BErroneousSymbol();
            return;
        }

        Symbol msymbol = tree.methodExpr.symbol;

        if (msymbol instanceof BMethodSymbol)
        {
            /* 找到唯一的函数 */
            BMethodSymbol methodSymbol = (BMethodSymbol) msymbol;
            /* 函数调用的返回值就是函数符号的返回值 */
            tree.symbol = methodSymbol.returnType;
            /* 检查形参和实参类型是否匹配 */
            if (SymbolSearcher.matchMethod(methodSymbol,argTypes) >= 0) {
                /* 设置查找到的方法符号 */
                tree.methodExpr.symbol = methodSymbol;
            }
            else
            {
                /* 参数不匹配错误处理 */
                tree.error( "方法'%s'的参数不匹配", msymbol.name);
                tree.methodExpr.symbol =new BErroneousSymbol();
            }
        }
        else if (msymbol instanceof BManySymbol) {
            BManySymbol moreSym = (BManySymbol) msymbol;
            /* 找到多个函数,按实参类型进行过滤，取最匹配的那个参数 */
            ArrayList<BMethodSymbol> methodSymbols = SymbolSearcher.filterMethods(moreSym , argTypes);
            if (methodSymbols.size() == 1) {
                BMethodSymbol methodSymbol = methodSymbols.get(0);
                tree.methodExpr.symbol = methodSymbol;
                tree.symbol = methodSymbol.getTypeSymbol();
            }
            else if (methodSymbols.size() == 0) {
                tree.error( "找不到合适的方法'%s'",tree.methodExpr.toString());
                tree.methodExpr.symbol =new BErroneousSymbol();
                tree.symbol= new BErroneousSymbol();
            }
            else {
                tree.error( "方法'%s'调用有歧义",tree.methodExpr.toString());
                tree.methodExpr.symbol =new BErroneousSymbol();
                tree.symbol= new BErroneousSymbol();
            }
        }
        else if(msymbol instanceof BErroneousSymbol)
            /* 如果是错误符号，不做处理 */
            tree.symbol= msymbol;
        else
        {
            tree.error( "'%s'不是函数,无法调用",tree.methodExpr.toString());
            tree.symbol= new BErroneousSymbol();
        }
    }
}
