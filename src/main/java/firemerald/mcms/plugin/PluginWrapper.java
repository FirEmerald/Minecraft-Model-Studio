package firemerald.mcms.plugin;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@CoreModExcluded
public class PluginWrapper extends AbstractPluginWrapper
{
	/**
	 * the plugin instance
	 */
	private Object plugin;

	/**
	 * @param candidate The plugin candidate
	 * @param className The plugin's class name - will be the {@link ICoreMod} instance for core mods!
	 * @param values a map of values, taken from the annotation's data
	 * 
	 * @see Plugin
	 */
	protected PluginWrapper(PluginCandidate candidate, String className, Map<String, Object> values)
	{
		super(candidate, className, values);
	}

	/**
	 * Constructs and returns the plugin instance. This is <i>not</i> the {@link ICoreMod} instance for core mods, but the associated plugin!
	 * 
	 * @return the plugin instance
	 * @throws InstantiationException if the plugin class is an abstract class.
	 * @throws InvocationTargetException if the plugin class's constructor throws an exception.
	 * @throws NoSuchMethodException if the plugin class does not have an empty public constructor.
	 * @throws ClassNotFoundException if the plugin class does not exist.
	 * @throws IllegalAccessException should never happen.
	 * @throws IllegalArgumentException should never happen.
	 * @throws SecurityException should never happen.
	 */
	@Override
	protected Object constructPlugin() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException
	{
		return plugin = Class.forName(className).getConstructor().newInstance();
	}

	@Override
	public Object getPlugin()
	{
		return plugin;
	}
}