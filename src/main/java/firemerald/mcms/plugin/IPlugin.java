package firemerald.mcms.plugin;

/**
 * You should implement this interface on your plugin file.<br>
 * This would be the main plugin file, generally where you initialize it, hook in to events, ect.<br>
 * You <bold>MUST<bold> have an constructor with no arguments for it to be constructed.<br>
 * any non-static methods with an {@link firemerald.mcms.events.EventHandler} annotation will be registered automatically to the main event bus.
 * using an interface that extends this one does NOT allow the plugin loader to automatically load it.
 * 
 * @author FirEmerald
 */
public interface IPlugin
{
	/**
	 * The name of this plugin.<br>
	 * Generally you would use Main.LANGUAGE.translate(String).
	 * 
	 * @return the plugin name
	 */
	public String name();
	
	/**
	 * the unique plugin ID for this plugin.<br>
	 * The game will not launch if there are two plugins with the same plugin ID present.<br>
	 * any {@link Resource} specific to this plugin should probably use this plugin ID.
	 * 
	 * @return the plugin ID
	 */
	public String pluginID();
	
	/**
	 * This is a convenience method for the player to see what version of the plugin they are using.<br>
	 * A plugin author could also implement an update checker as well.
	 * 
	 * @return the plugin version
	 */
	public String version();
	
	/** TODO size
	 * You can use this to have a custom plugin thumbnail in the plugins menu.<br>
	 * Keep in mind that this is a {@link String} representation of a {@link Resource}.
	 * 
	 * @return the resource location for the plugin's thumbnail, or null for none.
	 */
	public String thumbnail();
	
	/**
	 * The description for this plugin - a summary of what it does.<br>
	 * You can and should use line breaks.
	 * 
	 * @return the plugin description.
	 */
	public String description();
	
	/**
	 * The author of this plugin.
	 * 
	 * @return the plugin author
	 */
	public String author();
	
	/**
	 * An array of people, companies, ect. that deserve credit for the making of this plugin.<br>
	 * can include the plugin author ({@link #author()}), but doesn't need to.
	 * 
	 * @return an array of names
	 */
	public String[] credits();
}