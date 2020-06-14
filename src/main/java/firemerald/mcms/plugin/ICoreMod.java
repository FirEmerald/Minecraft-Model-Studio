package firemerald.mcms.plugin;

import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Instances of {@link ICoreMod} can be annotated with {@link CoreMod} for core modding
 * 
 * @author FirEmerald
 */
@CoreModExcluded
public interface ICoreMod
{
	/**
	 * A map of class names I.E. "firemerald.mcms.Main" to lists of {@link ICoreModder ICoreModders}.
	 * 
	 * @return a map of class names and associated core modders
	 */
	public @Nullable Map<String, List<ICoreModder>> getModders();
	
	/**
	 * A list of {@link ICoreModder ICoreModders} that will be run for all classes.
	 * 
	 * @return a list of core modders
	 */
	public @Nullable List<ICoreModder> getGlobalModders();
	
	/**
	 * Construct and return the main plugin class. If there isn't any main plugin class you should just return <code>this</code><br>
	 * The returned object will be run through {@link EventBus#registerListeners(Object handler)} to automatically register event listeners
	 * 
	 * @return the plugin class
	 */
	public @NonNull Object constructPlugin();
}