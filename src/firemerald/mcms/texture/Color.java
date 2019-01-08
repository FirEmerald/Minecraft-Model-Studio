package firemerald.mcms.texture;

import firemerald.mcms.util.MathUtil;
import firemerald.mcms.util.MiscUtil;

public class Color
{
	public static final Color RED = new Color(RGB.RED);
	public static final Color YELLOW = new Color(RGB.YELLOW);
	public static final Color GREEN = new Color(RGB.GREEN);
	public static final Color CYAN = new Color(RGB.CYAN);
	public static final Color BLUE = new Color(RGB.BLUE);
	public static final Color MAGENTA = new Color(RGB.MAGENTA);
	public static final Color BLACK = new Color(RGB.BLACK);
	public static final Color DARK_GREY = new Color(RGB.DARK_GREY);
	public static final Color GREY = new Color(RGB.GREY);
	public static final Color LIGHT_GREY = new Color(RGB.LIGHT_GREY);
	public static final Color WHITE = new Color(RGB.WHITE);
	public static final float MULT = 1f / 255f;
	
	public ColorModel c;
	public float a;
	
	public static Color parseColor(String str) throws NumberFormatException
	{
		str = str.trim();
		if (str.equalsIgnoreCase("RED")) return RED;
		else if (str.equalsIgnoreCase("YELLOW")) return YELLOW;
		else if (str.equalsIgnoreCase("GREEN")) return GREEN;
		else if (str.equalsIgnoreCase("CYAN")) return CYAN;
		else if (str.equalsIgnoreCase("BLUE")) return BLUE;
		else if (str.equalsIgnoreCase("MAGENTA")) return MAGENTA;
		else if (str.equalsIgnoreCase("BLACK")) return BLACK;
		else if (str.equalsIgnoreCase("DARK_GREY") || str.equalsIgnoreCase("DARK GREY") || str.equalsIgnoreCase("DARK_GRAY") || str.equalsIgnoreCase("DARK GRAY")) return DARK_GREY;
		else if (str.equalsIgnoreCase("GREY") || str.equalsIgnoreCase("GRAY")) return GREY;
		else if (str.equalsIgnoreCase("LIGHT_GREY") || str.equalsIgnoreCase("LIGHT GREY") || str.equalsIgnoreCase("LIGHT_GRAY") || str.equalsIgnoreCase("LIGHT GRAY")) return LIGHT_GREY;
		else if (str.equalsIgnoreCase("WHITE")) return WHITE;
		else
		{
			String[] args = MiscUtil.removeWhitespace(str.replaceAll(",", " ").split(" "));
			if (args.length == 0) throw new NumberFormatException("Color cannot be empty! " + str);
			else if (args.length == 1 && str.startsWith("0x")) return new Color(Integer.parseUnsignedInt(str.substring(2), 16));
			else return parseColor(args);
		}
	}
	
	public static Color parseColor(String[] args) throws NumberFormatException //R G B A or R G B or V A or V
	{
		float r, g, b, a;
		if (args.length >= 3)
		{
			r = parseValue(args[0]);
			g = parseValue(args[1]);
			b = parseValue(args[2]);
			a = args.length >= 4 ? parseValue(args[3]) : 1;
		}
		else
		{
			r = g = b = parseValue(args[0]);
			a = args.length >= 2 ? parseValue(args[1]) : 1;
		}
		return new Color(r, g, b, a);
	}
	
	public static float parseValue(String str) throws NumberFormatException
	{
		if (str.indexOf('.') >= 0) return Float.parseFloat(str);
		else if (str.startsWith("0x")) return Integer.parseUnsignedInt(str.substring(2), 16) * MULT;
		else return Integer.parseUnsignedInt(str) * MULT;
	}
	
	public Color(ColorModel c, float a)
	{
		this.c = c;
		this.a = a;
	}
	
	public Color(ColorModel c)
	{
		this(c, 1);
	}
	
	public Color(float r, float g, float b, float a)
	{
		this(new RGB(r, g, b), a);
	}
	
	public Color(float r, float g, float b)
	{
		this(r, g, b, 1);
	}
	
	public Color(float v, float a)
	{
		this(v, v, v, a);
	}
	
	public Color(float v)
	{
		this(v, 1);
	}
	
	public Color(int argb)
	{
		this(((argb >>> 16) & 255) * MULT, ((argb >>> 8) & 255) * MULT, (argb & 255) * MULT, (argb >>> 24) * MULT);
	}
	
	public Color()
	{
		this(1f);
	}
	
	public Color(Color col)
	{
		this(col.c, col.a);
	}

	public int toARGB()
	{
		RGB rgb = c.getRGB();
		return (MathUtil.clampInt(a, 0, 255) << 24) | (MathUtil.clampInt(rgb.r, 0, 255) << 16) | (MathUtil.clampInt(rgb.g, 0, 255) << 8) | MathUtil.clampInt(rgb.b, 0, 255); //assume no int has bits above index 8 due to clamping
	}

	public Color clamp()
	{
		return clamp(0, 1);
	}
	
	public Color clamp(float min, float max)
	{
		RGB rgb;
		c = rgb = c.getRGB();
		if (rgb.r < min) rgb.r = min;
		else if (rgb.r > max) rgb.r = max;
		if (rgb.g < min) rgb.g = min;
		else if (rgb.g > max) rgb.g = max;
		if (rgb.b < min) rgb.b = min;
		else if (rgb.b > max) rgb.b = max;
		if (a < min) a = min;
		else if (a > max) a = max;
		return this;
	}
	
	public static Color mix(Color a, Color b, float m)
	{
		RGB aRGB = a.c.getRGB();
		RGB bRGB = b.c.getRGB();
		return new Color(aRGB.r + (bRGB.r - aRGB.r) * m, aRGB.g + (bRGB.g - aRGB.g) * m, aRGB.b + (bRGB.b - aRGB.b) * m, a.a + (b.a - a.a) * m);
	}
}