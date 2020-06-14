package firemerald.mcms.plugin;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * The base plugin wrapper for loaded plugins
 * 
 * @author FirEmerald
 */
@CoreModExcluded
public abstract class AbstractPluginWrapper
{
	/**
	 * The plugin Candidate
	 */
	public final PluginCandidate candidate;
	/**
	 * The plugin's class name - will be the {@link ICoreMod} instance for core mods!
	 */
	public final String className;
	/**
	 * The plugin's human-readable display name
	 */
	public final String name;
	/**
	 * The plugin's internal ID
	 */
	public final String id;
	/**
	 * The plugin's main author
	 */
	public final String author;
	/**
	 * The plugin's version string
	 */
	public final String version;
	/**
	 * The plugin's description
	 */
	public final String description;
	/**
	 * The plugin's icon
	 */
	public final String icon;
	/**
	 * The plugin's credits
	 */
	public final String[] credits;
	/**
	 * The plugin's dependencies
	 */
	public final String[] dependencies;
	
	/**
	 * @param candidate The plugin candidate
	 * @param className The plugin's class name - will be the {@link ICoreMod} instance for core mods!
	 * @param values a map of values, taken from the annotation's data
	 * 
	 * @see Plugin
	 * @see CoreMod
	 */
	protected AbstractPluginWrapper(PluginCandidate candidate, String className, Map<String, Object> values)
	{
		this.candidate = candidate;
		this.className = className;
		id = (String) values.get("id");
		String name = (String) values.get("name");
		if (name == null || name.length() == 0) this.name = id;
		else this.name = name;
		String version = (String) values.get("version");
		if (version == null || version.length() == 0) this.version = "{version}";
		else this.version = version;
		String author = (String) values.get("author");
		if (author == null || author.length() == 0) this.author = "unknown";
		else this.author = author;
		String description = (String) values.get("description");
		if (description != null && description.length() == 0) this.description = null;
		else this.description = description;
		String icon = (String) values.get("icon");
		if (icon != null && icon.length() == 0) this.icon = null;
		else this.icon = icon;
		String[] credits = (String[]) values.get("credits");
		if (credits == null) this.credits = new String[0];
		else this.credits = credits;
		String[] dependencies = (String[]) values.get("dependencies");
		if (dependencies == null) this.dependencies = new String[0];
		else this.dependencies = dependencies;
	}
	
	/**
	 * Constructs and returns the plugin class. This is <i>not</i> the {@link ICoreMod} instance for core mods, but the associated plugin!
	 * 
	 * @return the plugin
	 */
	protected abstract Object constructPlugin() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException;

	/**
	 * Gets the plugin instance.
	 * 
	 * @return the plugin instance
	 */
	public abstract Object getPlugin();
	
	@Override
	public int hashCode()
	{
		return className.hashCode();
	}
}
