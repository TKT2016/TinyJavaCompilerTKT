package jtinyc.analyzers;
import java.util.ArrayList;
/* return树 */
class ReturnFlowNode
{
    /* 该节点是否已经标记为返回 */
    boolean exits =false;
    /* 父节点 */
    ReturnFlowNode parent;
    /* 子节点 */
    ArrayList<ReturnFlowNode> children = new ArrayList<>();

    /* 判断子节点是否全部标记返回 */
    public boolean isChildrenAllExist() {
        for (ReturnFlowNode sub : children) {
            if (!sub.exits)
                return false;
        }
        return true;
    }

    /* 标记当前节点已经退出，并且递归检查父节点 */
    public void setExitsTrue()
    {
        exits = true ;
        if(parent !=null && parent.isChildrenAllExist())
        {
            parent.setExitsTrue();
        }
    }

    /** 创建一个子节点并返回这个子节点 */
    public ReturnFlowNode createChild()
    {
        ReturnFlowNode newTree = new ReturnFlowNode();
        newTree.parent = this;
        this.children.add(newTree);
        return newTree;
    }
}