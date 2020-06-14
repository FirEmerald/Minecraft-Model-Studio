package firemerald.mcms.plugin;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@CoreModExcluded
public class CoreModWrapper extends AbstractPluginWrapper
{	
	/**
	 * the {@link ICoreMod} instance
	 */
	private ICoreMod coreMod;
	/**
	 * the plugin instance
	 */
	private Object plugin;

	/**
	 * @param candidate The plugin candidate
	 * @param className The plugin's class name - will be the {@link ICoreMod} instance for core mods!
	 * @param values a map of values, taken from the annotation's data
	 * 
	 * @see CoreMod
	 */
	protected CoreModWrapper(PluginCandidate candidate, String className, Map<String, Object> values)
	{
		super(candidate, className, values);
	}

	/**
	 * Constructs and returns the {@link ICoreMod} instance.
	 * 
	 * @return the {@link ICoreMod} instance
	 * @throws InstantiationException if the core mod class is an abstract class.
	 * @throws InvocationTargetException if the core mod class's constructor throws an exception.
	 * @throws NoSuchMethodException if the core mod class does not have an empty public constructor.
	 * @throws ClassNotFoundException if the core mod class does not exist.
	 * @throws ClassCastException if the core mod class is not an instance of {@link ICoreMod}.
	 * @throws IllegalAccessException should never happen.
	 * @throws IllegalArgumentException should never happen.
	 * @throws SecurityException should never happen.
	 */
	protected ICoreMod constructCoreMod() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException
	{
		return coreMod = (ICoreMod) Class.forName(className).getConstructor().newInstance();
	}

	/**
	 * Gets the {@link ICoreMod} instance.
	 * 
	 * @return the {@link ICoreMod} instance
	 */
	public ICoreMod getCoreMod()
	{
		return coreMod;
	}

	/**
	 * Constructs and return's the core mod's associated plugin instance from the {@link ICoreMod#constructPlugin()} method
	 * 
	 * @return the plugin instance
	 */
	@Override
	protected Object constructPlugin()
	{
		return plugin = coreMod.constructPlugin();
	}

	@Override
	public Object getPlugin()
	{
		return plugin;
	}
}