package firemerald.mcms.plugin;

import java.util.Comparator;

/**
 * Comparator for sorting {@link ICoreModder}s
 * 
 * @author FirEmerald
 *
 */
@CoreModExcluded
public class CoreModderComparator implements Comparator<ICoreModder>
{
	/**
	 * The name of the class about to be modded
	 */
	public final String name;
	
	/**
	 * constructs a new comparator for the class name
	 * 
	 * @param name the name of the class about to be modded
	 */
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