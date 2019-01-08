package firemerald.mcms.theme;

public class RoundedBoxFormat extends BoxFormat
{
	public final int radius;
	
	public RoundedBoxFormat(int w, int h)
	{
		this(w, h, 1, 0);
	}
	
	public RoundedBoxFormat(int w, int h, int radius)
	{
		this(w, h, 1, radius);
	}
	
	public RoundedBoxFormat(int w, int h, int outline, int radius)
	{
		super(w, h, outline);
		this.radius = radius;
		this.hash = ((w & 0xFFF) << 20) | ((h & 0xFFF) << 8) | ((outline & 0xF) << 4) | (radius & 0xF);
	}
	
	@Override
	public int hashCode()
	{
		return hash;
	}
	
	@Override
	public boolean equals(BoxFormat rect)
	{
		return super.equals(rect) && ((RoundedBoxFormat) rect).radius == this.radius;
	}
}