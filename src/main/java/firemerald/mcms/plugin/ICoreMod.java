package firemerald.mcms.plugin;

import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

/**
 * This interface denotes a plugin that also involves coremodding.<br>
 * Coremods will only be added from classes directly implementing {@link ICoreMod}.<br>
 * Keep in mind that this extends {@link IPlugin} and you do not need to also implement IPlugin yourself.<br>
 * Any errors during construction will cause the coremod to not be loaded - don't be afraid to use exceptions in the constructor to handle version conflicts!<br>
 * Also remember that this will be constructed before the game is launched. Avoid using <i>any</i> MCMS classes in the class - use a proxy instead.
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
	
	public @NonNull Object constructPlugin();
}