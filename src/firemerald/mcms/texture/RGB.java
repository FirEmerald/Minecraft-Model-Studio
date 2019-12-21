package firemerald.mcms.texture;

import firemerald.mcms.util.MathUtil;

public class RGB extends ColorModel
{
	public static final RGB RED = new RGB(1, 0, 0);
	public static final RGB YELLOW = new RGB(1, 1, 0);
	public static final RGB GREEN = new RGB(0, 1, 0);
	public static final RGB CYAN = new RGB(0, 1, 1);
	public static final RGB BLUE = new RGB(0, 0, 1);
	public static final RGB MAGENTA = new RGB(1, 0, 1);
	public static final RGB BLACK = new RGB(0);
	public static final RGB DARK_GREY = new RGB(.25f);
	public static final RGB GREY = new RGB(.5f);
	public static final RGB LIGHT_GREY = new RGB(.75f);
	public static final RGB WHITE = new RGB(1);
	
	public float r, g, b;
	
	public RGB(float val)
	{
		this(val, val, val);
	}
	
	public RGB(float r, float g, float b)
	{
		this.r = r;
		this.g = g;
		this.b = b;
	}

	@Override
	public RGB getRGB()
	{
		return this;
	}

	@Override
	public HSV getHSV()
	{
		float max, min;
		boolean flagR, flagG;
		//use a series of 2 or 3 if statements to get both max AND min at the same time - better than Math.max(Math.max(r, g), b) and Math.min(Math.min(r, g), b)
		if (r >= g) //r > g
		{
			if (g >= b) //r > g > b
			{
				max = r;
				min = b;
				flagR = true;
				flagG = false;
			}
			else if (r >= b) //r > b > g
			{
				max = r;
				min = g;
				flagR = true;
				flagG = false;
			}
			else //b > r > g
			{
				max = b;
				min = g;
				flagR = false;
				flagG = false;
			}
		}
		else
		{
			if (r >= b) //g > r > b
			{
				max = g;
				min = b;
				flagR = false;
				flagG = true;
			}
			else if (g >= b) //g > b > r
			{
				max = g;
				min = r;
				flagR = false;
				flagG = true;
			}
			else //b > g > r
			{
				max = b;
				min = r;
				flagR = false;
				flagG = false;
			}
		}
		float v = max;
		float d = max - min;
		float h = d == 0 ? 0 : (flagR ? (((g - b) / d) % 6) : flagG ? (((b - r) / d) + 2) : (((r - g) / d) + 4)) / 6;
		if (h < 0) h++;
		float s = d == 0 || v == 0 ? 0 : d / v;
		return new HSV(h, s, v);
	}

	@Override
	public HSL getHSL()
	{
		float max, min;
		boolean flagR, flagG;
		//use a series of 2 or 3 if statements to get both max AND min at the same time - better than Math.max(Math.max(r, g), b) and Math.min(Math.min(r, g), b)
		if (r >= g) //r > g
		{
			if (g >= b) //r > g > b
			{
				max = r;
				min = b;
				flagR = true;
				flagG = false;
			}
			else if (r >= b) //r > b > g
			{
				max = r;
				min = g;
				flagR = true;
				flagG = false;
			}
			else //b > r > g
			{
				max = b;
				min = g;
				flagR = false;
				flagG = false;
			}
		}
		else
		{
			if (r >= b) //g > r > b
			{
				max = g;
				min = b;
				flagR = false;
				flagG = true;
			}
			else if (g >= b) //g > b > r
			{
				max = g;
				min = r;
				flagR = false;
				flagG = true;
			}
			else //b > g > r
			{
				max = b;
				min = r;
				flagR = false;
				flagG = false;
			}
		}
		float d = max - min;
		float h = d == 0 ? 0 : (flagR ? (((g - b) / d) % 6) : flagG ? (((b - r) / d) + 2) : (((r - g) / d) + 4)) / 6;
		if (h < 0) h++;
		float l = (max + min) * .5f;
		float s = d == 0 || l == 0 || l == 1 ? 0 : d / (1 - Math.abs(2 * l - 1));
		return new HSL(h, s, l);
	}

	@Override
	public RGB copy()
	{
		return new RGB(r, g, b);
	}
	
	@Override
	public int hashCode()
	{
		return (MathUtil.clampInt(r, 0, 255) << 16) | (MathUtil.clampInt(g, 0, 255) << 8) | MathUtil.clampInt(b, 0, 255); //assume no int has bits above index 8 due to clamping
	}
	
	@Override
	public boolean equals(Object o)
	{
		return o instanceof ColorModel && ((ColorModel) o).getRGB().hashCode() == hashCode();
	}
}