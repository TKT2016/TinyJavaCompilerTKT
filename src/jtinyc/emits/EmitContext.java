package jtinyc.emits;
import org.objectweb.asm.*;
/**源文件生成上下文参数 */
class EmitContext
{
    /** ASM字节码当前类型写入器 */
    public ClassWriter classWriter;
    /** 当前函数ASM字节码写入器 */
    public MethodVisitor mv;
    /** 当前方法结束标签 */
    public Label methodEndLabel;
    /** 行号生成器 */
    public LineNumberEmit lineNumberEmit;
}