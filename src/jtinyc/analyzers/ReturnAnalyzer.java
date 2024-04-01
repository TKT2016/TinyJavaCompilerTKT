package jtinyc.analyzers;
import jtinyc.symbols.SymbolUtil;
import jtinyc.trees.TreeScanner;
import jtinyc.trees.*;

/** return语句返回结果分析 */
public class ReturnAnalyzer extends TreeScanner<ReturnFlowNode> {

    public void visit(JCFile tree)
    {
        this.visitFile( tree,null);
    }

    @Override
    public void visitMethodDef(JCMethod tree, ReturnFlowNode arg)
    {
        /* 当前函数没有返回值，就不需要做return检查 */
        if(SymbolUtil.isVoid(tree.methodSymbol.returnType))
            return;
        /* 创建函数返回节点 */
        ReturnFlowNode returnFlowNode = new ReturnFlowNode();
        /* 代入函数返回节点对函数体进行分析 */
        tree.body.scan(this, returnFlowNode);
        /*分析结束，函数返回节点没有标记返回则报错 */
        if(!returnFlowNode.exits)
        {
            tree.error( tree.nameToken,"'%s'缺少返回语句","return");
        }
    }

    /* 分析if语句的返回*/
    @Override
    public void visitIf(JCIf tree, ReturnFlowNode arg)
    {
        /*
            在选择语句中,必须用父节点arg创建两个分支节点: thenNode 和 elseNode
            不管if语句是否存在else部分，都必须创建elseNode
            必须先创建好分支节点，再进行分析，否则可能出错
         */
        ReturnFlowNode thenNode = arg.createChild();
        ReturnFlowNode elseNode = arg.createChild();

        /* 用thenNode分析then分支语句 */
        tree.thenpart.scan(this,thenNode);
        /* 如果有else部分，用elseNode分析else部分 */
        if(tree.elsepart!=null)
            tree.elsepart.scan(this,elseNode);
    }

    /* 分析return语句返回 */
    @Override
    public void visitReturn(JCReturn tree, ReturnFlowNode arg)
    {
        /*此处是return语句，表明这部分语句块有返回结果，调用setExitsTrue做标记 */
        arg.setExitsTrue();
    }
}
