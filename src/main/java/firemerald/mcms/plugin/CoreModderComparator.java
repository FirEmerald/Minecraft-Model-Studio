package firemerald.mcms.plugin;

import java.util.Comparator;

public class CoreModderComparator implements Comparator<ICoreModder>
{
	public final String name;
	
	public CoreModderComparator(String name)
	{
		this.name = name;
	}
	
	@Override
	public int compare(ICoreModder o1, ICoreModder o2)
	{
		return o1.getPriority(name) - o2.getPriority(name);
	}
}