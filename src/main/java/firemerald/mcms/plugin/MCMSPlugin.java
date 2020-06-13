package firemerald.mcms.plugin;

import firemerald.mcms.Main;
import firemerald.mcms.util.ResourceLocation;
import firemerald.mcms.util.font.Formatting;

/**
 * This is the IPlugin for the application's data.
 * 
 * @author FirEmerald
 */
public class MCMSPlugin implements IPlugin
{
	public static final MCMSPlugin INSTANCE = new MCMSPlugin();
	
	@Override
	public String name()
	{
		return "Minecraft Model Studio";
	}

	@Override
	public String pluginID()
	{
		return "mcms";
	}

	@Override
	public String version()
	{
		return Main.VERSION;
	}
	
	public static final ResourceLocation ICON = new ResourceLocation(Main.ID, "icon.png");

	@Override
	public ResourceLocation thumbnail()
	{
		return ICON;
	}

	@Override
	public String description()
	{
		return "Modeling Studio for Minecraft";
	}

	@Override
	public String author()
	{
		return Formatting.FIREMERALD;
	}

	@Override
	public String[] credits()
	{
		return null;
	}
}