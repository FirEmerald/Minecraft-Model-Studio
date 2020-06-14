package firemerald.mcms.plugin;

/**
 * This interface should be used on classes that will be transforming other classes via coremodding.
 * 
 * @author FirEmerald
 */
@FunctionalInterface
public interface ICoreModder
{
	/**
	 * Use ObjectWeb ASM (or another ASM library, if you add it to the classloader) to transform a class.
	 * 
	 * @param name the class name
	 * @param bytes the class's binary contents
	 * @return the modified binary contents
	 */
	public byte[] coreMod(String name, byte[] bytes);
	
	public default int getPriority(String name)
	{
		return 0;
	}
}