package firemerald.mcms.plugin;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class CoreModWrapper extends AbstractPluginWrapper
{	
	private ICoreMod coreMod;
	private Object plugin;
	
	public CoreModWrapper(PluginCandidate candidate, String className, Map<String, Object> values)
	{
		super(candidate, className, values);
	}

	protected ICoreMod constructCoreMod() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException
	{
		return coreMod = (ICoreMod) Class.forName(className).getConstructor().newInstance();
	}
	
	public ICoreMod getCoreMod()
	{
		return coreMod;
	}

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