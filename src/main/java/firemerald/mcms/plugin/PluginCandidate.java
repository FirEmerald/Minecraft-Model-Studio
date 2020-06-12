package firemerald.mcms.plugin;

import java.io.File;

public class PluginCandidate
{
	public final File file;
	public final boolean isSource;
	public boolean isPlugin;
	
	public PluginCandidate(File file, boolean isSource)
	{
		this.file = file;
		isPlugin = this.isSource = isSource;
	}
}