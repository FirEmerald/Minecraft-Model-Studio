package firemerald.mcms.texture;

public abstract class BlendMode
{
	public static final BlendMode NORMAL = new BlendNormal();
	public static final BlendMode ADD = new BlendAdd();
	
	public abstract Color blend(Color src, Color des);
	
	private static class BlendNormal extends BlendMode
	{
		@Override
		public Color blend(Color src, Color des)
		{
			float inva = 1 - src.a;
			RGB rgbSrc = src.c.getRGB();
			RGB rgbDes = des.c.getRGB();
			float r, g, b, a = src.a + des.a * inva;
			if (a != 0)
			{
				float inva2 = 1 / a;
				r = (rgbSrc.r * src.a + rgbDes.r * des.a * inva) * inva2;
				g = (rgbSrc.g * src.a + rgbDes.g * des.a * inva) * inva2;
				b = (rgbSrc.b * src.a + rgbDes.b * des.a * inva) * inva2;
			}
			else
			{
				r = rgbDes.r;
				g = rgbDes.g;
				b = rgbDes.b;
			}
			return new Color(r, g, b, a);
		}
	}
	
	private static class BlendAdd extends BlendMode
	{
		@Override
		public Color blend(Color src, Color des)
		{
			RGB rgbSrc = src.c.getRGB();
			RGB rgbDes = des.c.getRGB();
			return new Color(rgbSrc.r + rgbDes.r, rgbSrc.g + rgbDes.g, rgbSrc.b + rgbDes.b, des.a);
		}
	}
}