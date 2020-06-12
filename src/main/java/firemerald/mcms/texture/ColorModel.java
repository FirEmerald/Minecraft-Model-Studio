package firemerald.mcms.texture;

public abstract class ColorModel
{
	public abstract RGB getRGB();

	public abstract HSV getHSV();

	public abstract HSL getHSL();
	
	public abstract ColorModel copy();
	
	@Override
	public int hashCode()
	{
		return getRGB().hashCode();
	}
	
	@Override
	public boolean equals(Object o)
	{
		return getRGB().equals(o);
	}
}