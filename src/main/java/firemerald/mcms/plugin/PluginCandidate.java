package firemerald.mcms.plugin;

import java.io.File;

/**
 * Represents a file or directory that may contain plugins
 * 
 * @author FirEmerald
 *
 */
@CoreModExcluded
public class PluginCandidate
{
	/**
	 * The file or directory this candidate represents
	 */
	public final File file;
	/**
	 * If the candidate was found in the classpath
	 */
	public final boolean isSource;
	/**
	 * If a plugin was found in the file or directory
	 */
	public boolean isPlugin;
	
	/**
	 * Constructs a new plugin candidate
	 * 
	 * @param file the file or directory this candidate represents
	 * @param isSource if the candidate was found in the classpath
	 */
	public PluginCandidate(File file, boolean isSource)
	{
		this.file = file;
		isPlugin = this.isSource = isSource;
	}
}