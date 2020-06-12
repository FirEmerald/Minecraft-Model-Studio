package firemerald.mcms.texture;

public enum BlendMode
{
	NORMAL
	{
		@Override
		public Color blend(Color src, Color des)
		{
			if (src.a == 0) return des;
			else if (src.a == 1 || des.a == 0) return src;
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
	},
	ADD
	{
		@Override
		public Color blend(Color src, Color des)
		{
			RGB rgbSrc = src.c.getRGB();
			RGB rgbDes = des.c.getRGB();
			return new Color(rgbSrc.r + rgbDes.r, rgbSrc.g + rgbDes.g, rgbSrc.b + rgbDes.b, des.a);
		}
	},
	MULTIPLY
	{
		@Override
		public Color blend(Color src, Color des)
		{
			RGB rgbSrc = src.c.getRGB();
			RGB rgbDes = des.c.getRGB();
			return new Color(rgbSrc.r * rgbDes.r, rgbSrc.g * rgbDes.g, rgbSrc.b * rgbDes.b, src.a * des.a);
		}
	},
	SUBTRACT
	{
		@Override
		public Color blend(Color src, Color des)
		{
			RGB rgbSrc = src.c.getRGB();
			RGB rgbDes = des.c.getRGB();
			return new Color(rgbDes.r - rgbSrc.r, rgbDes.g - rgbSrc.g, rgbDes.b - rgbSrc.b, des.a);
		}
	},
	DIVIDE
	{
		@Override
		public Color blend(Color src, Color des)
		{
			RGB rgbSrc = src.c.getRGB();
			RGB rgbDes = des.c.getRGB();
			return new Color(rgbSrc.r == 0 ? 1 : rgbDes.r / rgbSrc.r, rgbSrc.g == 0 ? 1 : rgbDes.g / rgbSrc.g, rgbSrc.b == 0 ? 1 : rgbDes.b / rgbSrc.b, src.a == 0 ? 1 : des.a / src.a);
		}
	},
	HSV
	{
		@Override
		public Color blend(Color src, Color des)
		{
			HSV hsvSrc = src.c.getHSV();
			HSV hsvDes = des.c.getHSV();
			return new Color(new HSV(hsvSrc.h + hsvDes.h, hsvSrc.s * hsvDes.s, hsvSrc.v * hsvDes.v), des.a);
		}
	},
	HSL
	{
		@Override
		public Color blend(Color src, Color des)
		{
			HSL hslSrc = src.c.getHSL();
			HSL hslDes = des.c.getHSL();
			return new Color(new HSV(hslSrc.h + hslDes.h, hslSrc.s * hslDes.s, hslSrc.l * hslDes.l), des.a);
		}
	};
	
	public abstract Color blend(Color src, Color des);
}