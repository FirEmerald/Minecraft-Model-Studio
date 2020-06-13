package firemerald.mcms.plugin;

import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;

/**
 * This interface denotes a plugin that also involves coremodding.<br>
 * Keep in mind that this extends {@link IPlugin} and you should not, <bold>UNDER ANY CIRCUMSTANCES<bold>, also implement IPlugin yourself.<br>
 * Also remember that this will be constructed before the game is launched. Avoid using <i>any</i> MCMS classes in the class - use a proxy instead.
 * using an interface that extends this one does NOT allow the plugin loader to automatically load it.
 * 
 * @author FirEmerald
 */
public interface ICoreMod extends IPlugin
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
}