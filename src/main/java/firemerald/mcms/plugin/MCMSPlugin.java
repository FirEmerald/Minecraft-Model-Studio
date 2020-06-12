package firemerald.mcms.plugin;

import firemerald.mcms.Main;

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

	@Override
	public String thumbnail() //TODO
	{
		return "mcms_icon";
	}

	@Override
	public String description()
	{
		return "The base program";
	}

	@Override
	public String author()
	{
		return "FirEmerald";
	}

	@Override
	public String[] credits()
	{
		return new String[] {"FirEmerald"};
	}
}