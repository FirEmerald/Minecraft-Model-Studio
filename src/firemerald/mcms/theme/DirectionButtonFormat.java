package firemerald.mcms.theme;

public class DirectionButtonFormat extends BoxFormat
{
	public final int direction;
	
	public DirectionButtonFormat(int w, int h, int direction)
	{
		this(w, h, 1, direction);
	}
	
	public DirectionButtonFormat(int w, int h, int outline, int direction)
	{
		super(w, h, outline);
		this.direction = direction;
		this.hash = ((w & 0xFFF) << 20) | ((h & 0xFFF) << 8) | ((outline & 0xFC) << 4) | (direction & 0x3);
	}
	
	@Override
	public int hashCode()
	{
		return hash;
	}
	
	@Override
	public boolean equals(BoxFormat rect)
	{
		return super.equals(rect) && ((DirectionButtonFormat) rect).direction == this.direction;
	}
}