package firemerald.mcms.plugin;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@CoreModExcluded
public abstract class AbstractPluginWrapper
{
	public final PluginCandidate candidate;
	public final String className, name, id, author, version, description, icon;
	public final String[] credits;
	
	public AbstractPluginWrapper(PluginCandidate candidate, String className, Map<String, Object> values)
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
	}
	
	protected abstract Object constructPlugin() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException;
	
	public abstract Object getPlugin();
	
	@Override
	public int hashCode()
	{
		return className.hashCode();
	}
}
