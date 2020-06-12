package firemerald.mcms.plugin;

/**
 * This interface denotes a plugin that shouldn't be loaded by the pluginloader.<br>
 * Use it for optional plugins inside plugins that should only be loaded under certain circumstances, such as in the presence of other plugins.
 * 
 * @author FirEmerald
 */
public interface IOptionalPlugin extends IPlugin {}