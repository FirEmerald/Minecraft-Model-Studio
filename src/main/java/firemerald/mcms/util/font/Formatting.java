package firemerald.mcms.util.font;

import firemerald.mcms.texture.Color;

public class Formatting
{
	public static final char CHAR_SELECTOR = '§';
	public static final char CHAR_COLOR = 'c';
	public static final char CHAR_FONT = 'f';
	public static final char CHAR_UNDERLINE = 'u';
	public static final char CHAR_STRIKETHROUGH = 's';
	public static final char CHAR_ITALIC = 'i';
	public static final char CHAR_BOLD = 'b';
	public static final char CHAR_RESET = 'r';

	public static final String SELECTOR = "§";
	public static final String COLOR = SELECTOR + CHAR_COLOR;
	public static final String FONT = SELECTOR + CHAR_FONT;
	public static final String UNDERLINE = SELECTOR + CHAR_UNDERLINE;
	public static final String STRIKETHROUGH = SELECTOR + CHAR_STRIKETHROUGH;
	public static final String ITALIC = SELECTOR + CHAR_ITALIC;
	public static final String BOLD = SELECTOR + CHAR_BOLD;
	public static final String RESET = SELECTOR + CHAR_RESET;
	
	public static final String FIREMERALD = "§cFF0000Fir§cFFFF00E§c00FF00merald";
	
	public static String color(float r, float g, float b)
	{
		return color(Math.round(r * 255), Math.round(g * 255), Math.round(b * 255));
	}
	
	public static String color(int r, int g, int b)
	{
		if (r < 0) r = 0;
		else if (r > 255) r = 255;
		if (g < 0) g = 0;
		else if (g > 255) g = 255;
		if (b < 0) b = 0;
		else if (b > 255) b = 255;
		return color((r << 16) | (g << 8) | b);
	}
	
	public static String color(Color color)
	{
		return color(color.toARGB() & 0xFFFFFF);
	}
	
	public static String color(int rgb)
	{
		String str = Integer.toString(rgb, 16);
		if (str.length() > 6) str = str.substring(str.length() - 6);
		else while (str.length() < 6) str = "0" + str;
		return Formatting.COLOR + str;
	}
}