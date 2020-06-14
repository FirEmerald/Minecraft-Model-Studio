package firemerald.mcms.plugin;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class PluginWrapper extends AbstractPluginWrapper
{
	public final String[] dependencies;
	private Object plugin;
	
	public PluginWrapper(PluginCandidate candidate, String className, Map<String, Object> values)
	{
		super(candidate, className, values);
		String[] dependencies = (String[]) values.get("dependencies");
		if (dependencies == null) this.dependencies = new String[0];
		else this.dependencies = dependencies;
	}
	
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