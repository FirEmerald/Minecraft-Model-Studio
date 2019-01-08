package firemerald.mcms.theme;

public class BoxFormat
{
	public final int w, h, outline;
	protected int hash;
	
	public BoxFormat(int w, int h)
	{
		this(w, h, 1);
	}
	
	public BoxFormat(int w, int h, int outline)
	{
		this.w = w;
		this.h = h;
		this.outline = outline;
		this.hash = ((w & 0xFFF) << 20) | ((h & 0xFFF) << 8) | (outline & 0xFF);
	}
	
	@Override
	public int hashCode()
	{
		return hash;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o == this) return true;
		else if (o.getClass() != this.getClass()) return false;
		else return equals((BoxFormat) o);
	}
	
	public boolean equals(BoxFormat rect)
	{
		return rect.w == this.w && rect.h == this.h && rect.outline == this.outline;
	}
}