package jtinyc.analyzers;
import jtinyc.symbols.*;
import jtinyc.trees.*;
import tools.FileUtil;
import java.util.HashMap;
/** 文件结构语义分析器 */
public class FileAnalyzer {
    /* 分析文件内包定义导入方法等 */
    public void visitFile(JCFile tree)
    {
        tree.fileSymbol = new DSourceFileSymbol(FileUtil.getNameNoExt( tree.log.sourceFile));;
        tree.symbolScope = new SymbolScope(null); // 创建顶层的文件作用域实例
        /* 分析package */
        visitPackageDef(tree.packageDecl, tree.fileSymbol );

        /* 分析import */
        /* importTypes 用于检测是否重复导入类型*/
        HashMap<String, RClassSymbol> importTypes = new HashMap<>();
        for(JCImport jcImport : tree.imports)
            visitImport(jcImport,tree,importTypes);
        /* 分析函数 */
        for(JCMethod methodDecl :tree.methods)
            visitMethodDef(methodDecl,tree.fileSymbol,tree.symbolScope);
    }

     /* 分析package定义 */
    private void visitPackageDef(JCPackage jcPackage, DSourceFileSymbol fileSymbol)
    {
        if(jcPackage==null) return;
        if(fileSymbol.packageName==null)
        {
            fileSymbol.packageName = jcPackage.packageName.getFullName();
        }
    }

     /* 分析import */
    private void visitImport(JCImport jcImport, JCFile fileTree, HashMap<String, RClassSymbol> importTypes) {
        /* 生成类型的全名称 */
        String fullName = jcImport.typeTree.getFullName();
        /* 检查是否已经导入 */
        if (importTypes.containsKey(fullName)) {
            jcImport.log.error(jcImport.typeTree.getPosToken(), String.format("'%s'重复导入", fullName));
            return;
        }
        /* 根据类型全名称创建类型符号,如果创建结果为null,说明找不到这个类型，要报错 */
        RClassSymbol classSymbol = RClassSymbolManager.forName(fullName);
        if (classSymbol == null) {
            jcImport.log.error(jcImport.typeTree.getPosToken(), String.format("类型'%s'不存在", fullName));
            return;
        }

        /* 加入导入的类型 */
        importTypes.put(fullName, classSymbol);
        fileTree.symbolScope.addSymbol(classSymbol);
    }

     /* 分析函数 */
    void visitMethodDef(JCMethod tree, DSourceFileSymbol fileSymbol, SymbolScope fileScope) {
        tree.scope = fileScope.createChild(); //创建函数体作用域实例，父级是文件作用域
        BTypeSymbol retTypeSymbol = SymbolSearcher.findType( tree.retTypeExpr, fileScope);

        tree.methodSymbol =  new DMethodSymbol(fileSymbol,tree.nameToken.identName , retTypeSymbol);

        for (JCVariableDecl variableDecl : tree.params)
            visitParameter(variableDecl,  tree. methodSymbol,tree.scope);

        if (!fileSymbol.addMethod(tree.methodSymbol))
        {
            tree.error(tree.nameToken,"已经定义了方法 '%s'",tree.nameToken.identName);
        }
        else
        {
            fileScope.addSymbol(tree.methodSymbol);
        }
    }

     /* 分析函数参数 */
    void visitParameter(JCVariableDecl tree, DMethodSymbol methodSymbol, SymbolScope scope) {
        BTypeSymbol typeSymbol =  SymbolSearcher.findType(tree.varType, scope);

        String parameterName = tree.nameExpr.getName();
        if (methodSymbol.parameters.contains(parameterName))
        {
            tree.error(tree.nameExpr.nameToken,"方法已经定义了参数 '%s'", parameterName);
            DVarSymbol paramSymbol = methodSymbol.parameters.get(parameterName);
            tree.symbol=paramSymbol;
        }
        else {
            DVarSymbol paramSymbol = new DVarSymbol( parameterName,  VarSymbolKind.parameter, typeSymbol);
            methodSymbol.addParameter(paramSymbol);
            scope.addSymbol(paramSymbol);
        }
    }
}
