package jtinyc.optimizers;

import jtinyc.trees.JCFile;

public class Optimizer {

    public JCFile translate(JCFile tree) {

        JCFile treetemp = tree;

        /* 常量折叠优化 */
        treetemp = (new ConstFoldOptimizer()).translate(treetemp);

        /* 常量传播优化 */
        treetemp = ( new ConstSpreadOptimizer()).translate(treetemp);

        /* 赋值优化 */
        treetemp = (new AssignOptimizer()).translate(treetemp);

        /* return语句后死代码消除 */
        treetemp = (new ReturnDeadCodeOptimizer()).translate(treetemp);

        /* 死代码消除 */
        treetemp = (new DeadCodeOptimizer()).translate(treetemp);

        return treetemp;
    }
}
