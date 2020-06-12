package firemerald.mcms.util;

import firemerald.mcms.Main;

public class ResourceLocation
{
	private final String domain, path, location;
	
	public ResourceLocation(String path)
	{
		int locColon = path.indexOf(':');
		int locSlash = path.indexOf('/');
		if (locColon >= 0 && (locSlash < 0 || locSlash > locColon))
		{
			this.domain = path.substring(0, locColon);
			this.path = path.substring(locColon + 1);
		}
		else
		{
			this.domain = Main.ID;
			this.path = path;
		}
		this.location = "assets/" + this.domain + "/" + this.path;
	}
	
	public ResourceLocation(String domain, String path)
	{
		this.domain = domain;
		this.path = path;
		this.location = "assets/" + domain + "/" + path;
	}
	
	public String getDomain()
	{
		return domain;
	}
	
	public String getPath()
	{
		return path;
	}
	
	public String getLocation()
	{
		return location;
	}
	
	@Override
	public int hashCode()
	{
		return location.hashCode();
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o == this) return true;
		else if (o.getClass() != this.getClass()) return false;
		else return ((ResourceLocation) o).location.equals(this.location);
	}
	
	public ResourceLocation prependPath(String prepend)
	{
		return new ResourceLocation(domain, prepend + path);
	}
	
	public ResourceLocation appendPath(String append)
	{
		return new ResourceLocation(domain, path + append);
	}
	
	@Override
	public String toString()
	{
		return domain + ":" + path;
	}
}