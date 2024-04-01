package jtinyc.trees;

/** 导入包或类型语句 */
public class JCImport extends JCTree
{
    /** 导入的类型 */
    public JCFullname typeTree;

    @Override
    public <D> void scan(TreeScanner<D> v, D arg) {
        v.visitImport(this, arg);
    }

    @Override
    public <D> JCTree translate(TreeTranslator<D> v, D arg) {
        return v.translateImport(this, arg);
    }
}
