package firemerald.mcms.plugin;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

/**
 * Utility class used to convert between binary data and {@link ClassNode ClassNodes}.
 * 
 * @author FirEmerald
 */
@CoreModExcluded
public class ASMUtil
{
    
    /**
     * Creates a {@link ClassNode} from the binary data.
     * 
     * @param clazz
     *            the bytes of this class.
     * @param flags
     *            option flags that can be used to modify the default behavior of this class. See 
     *            {@link ClassReader#SKIP_DEBUG}, 
     *            {@link ClassReader#EXPAND_FRAMES}, 
     *            {@link ClassReader#SKIP_FRAMES}, 
     *            {@link ClassReader#SKIP_CODE}.
     */
	public static ClassNode getNode(byte[] clazz, int flags)
	{
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(clazz);
		classReader.accept(classNode, flags);
		return classNode;
	}
    
    /**
     * Gets the binary data from a {@link ClassNode}.
     * 
     * @param classNode
     *            the ClassNode of this class.
     * @param flags
     *            option flags that can be used to modify the default behavior of this class. See
     *            {@link ClassWriter#COMPUTE_MAXS},
     *            {@link ClassWriter#COMPUTE_FRAMES}.
     */
	public static byte[] getClass(ClassNode classNode, int flags)
	{
		ClassWriter writer = new ClassWriter(flags);
		classNode.accept(writer);
		return writer.toByteArray();
	}
}