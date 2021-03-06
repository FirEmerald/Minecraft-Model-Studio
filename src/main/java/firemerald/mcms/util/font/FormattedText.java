package firemerald.mcms.util.font;

import java.util.ArrayList;
import java.util.List;

import firemerald.mcms.texture.Color;
import firemerald.mcms.util.ResourceLocation;

public class FormattedText
{
	public FontRenderer font;
	public boolean bold, italic, underline, strikethrough;
	public Color color;
	public String text;
	public FormattedText appended;
	
	public FormattedText(String text, FontRenderer font)
	{
		this(text, Color.BLACK, false, false, false, false, font);
	}
	
	public FormattedText(String text, Color color, FontRenderer font)
	{
		this(text, color, false, false, false, false, font);
	}
	
	public FormattedText(String text, Color color, boolean bold, boolean italic, boolean underline, boolean strikethrough, FontRenderer font)
	{
		this.text = text;
		this.color = color;
		this.bold = bold;
		this.italic = italic;
		this.underline = underline;
		this.strikethrough = strikethrough;
		this.font = font;
	}
	
	public FormattedText append(String text)
	{
		return append(text, null, null, null, null, null, null);
	}
	
	public FormattedText append(String text, Color color)
	{
		return append(text, color, null, null, null, null, null);
	}
	
	public FormattedText append(String text, FontRenderer font)
	{
		return append(text, null, null, null, null, null, font);
	}
	
	public FormattedText append(String text, Color color, FontRenderer font)
	{
		return append(text, color, null, null, null, null, font);
	}
	
	public FormattedText append(String text, Color color, Boolean bold, Boolean italic, Boolean underline, Boolean strikethrough, FontRenderer font)
	{
		return append(new FormattedText(text, color == null ? this.color : color, bold == null ? this.bold : bold, italic == null ? this.italic : italic, underline == null ? this.underline : underline, strikethrough == null ? this.strikethrough : strikethrough, font == null ? this.font : font));
	}
	
	public FormattedText append(FormattedText text)
	{
		if (this.appended != null) appended.append(text);
		else if (text.color.equals(this.color) && text.bold == this.bold && text.italic == this.italic && text.underline == this.underline && text.strikethrough == this.strikethrough && text.font == this.font) this.text += text.text;
		else this.appended = text;
		return this;
	}
	
	private FormattedText append2(FormattedText text)
	{
		if (this.appended != null) return appended.append2(text);
		else if (text.color.equals(this.color) && text.bold == this.bold && text.italic == this.italic && text.underline == this.underline && text.strikethrough == this.strikethrough && text.font == this.font)
		{
			this.text += text.text;
			return this;
		}
		else
		{
			this.appended = text;
			return text;
		}
	}
	
	public static FormattedText parse(String text, FontRenderer font, Color color, boolean shadow, boolean allowFontChange)
	{
		Color original = color;
		FormattedText formatted = null;
		FormattedText formatting = null;
		StringBuilder builder = null;
		boolean italic = false, bold = false, strikethrough = false, underline = false;
		char c;
		int p = 0;
		int length = text.length();
		boolean needsUpdate = false;
		while (p < length)
		{
			c = text.charAt(p);
			if (c == Formatting.CHAR_SELECTOR)
			{
				c = text.charAt(++p);
				p++;
				switch (c)
				{
				case Formatting.CHAR_COLOR:
					int col = Integer.parseInt(text.substring(p, p + 6), 16);
					float r = ((col >> 16) & 255) / 255f;
					float g = ((col >> 8) & 255) / 255f;
					float b = (col & 255) / 255f;
					color = new Color(r, g, b, color.a);
					p += 6;
					needsUpdate = true;
					break;
				case Formatting.CHAR_FONT:
					if (allowFontChange)
					{
						int end = text.indexOf(';', p);
						if (end != -1)
						{
							ResourceLocation fontname = new ResourceLocation(text.substring(p, end));
							p = end + 1;
							if (!font.name.equals(fontname) && FontRenderer.FONTS.containsKey(fontname))
							{
								font = FontRenderer.FONTS.get(fontname);
								needsUpdate = true;
							}
						}
					}
					break;
				case Formatting.CHAR_UNDERLINE:
					underline = !underline;
					needsUpdate = true;
					break;
				case Formatting.CHAR_STRIKETHROUGH:
					strikethrough = !strikethrough;
					needsUpdate = true;
					break;
				case Formatting.CHAR_ITALIC:
					italic = !italic;
					needsUpdate = true;
					break;
				case Formatting.CHAR_BOLD:
					bold = !bold;
					needsUpdate = true;
					break;
				case Formatting.CHAR_RESET:
					color = original;
					underline = strikethrough = italic = bold = false;
					needsUpdate = true;
					break;
				}
			}
			else
			{
				if (formatted == null)
				{
					formatted = formatting = new FormattedText("", color, bold, italic, underline, strikethrough, font);
					needsUpdate = false;
					builder = new StringBuilder();
				}
				else if (needsUpdate)
				{
					formatting.text += builder.toString();
					formatting = formatting.append2(new FormattedText("", color, bold, italic, underline, strikethrough, font));
					builder = new StringBuilder();
				}
				builder.append(c);
				p++;
			}
		}
		formatting.text += builder.toString();
		return formatted;
	}
	
	public FormattedText copy()
	{
		FormattedText copy = new FormattedText(text, color, bold, italic, underline, strikethrough, font);
		if (appended != null) copy.append(appended.copy());
		return copy;
	}
	
	public FormattedText splitForWidth(final float width)
	{
		int w = 0, wW = 0;
		FormattedText text = this;
		while (text != null)
		{
			String s = text.text;
			FontRenderer font = text.font;
			StringBuilder str = new StringBuilder();
			StringBuilder word = new StringBuilder();
			List<String> strs = new ArrayList<String>();
			for (int pos = 0; pos < s.length(); pos++)
			{
				char c = s.charAt(pos);
				int cw = font.widths[c];
				if (text.bold) cw++;
				if (c == '\n') //new line
				{
					str.append(word);
					//str.append(c);
					strs.add(str.toString());
					str = new StringBuilder();
					word = new StringBuilder();
					w = 0;
					wW = 0;
				}
				else if (Character.isWhitespace(c)) //whitespace
				{
					str.append(word);
					str.append(c);
					word = new StringBuilder();
					w += cw;
					wW = 0;
				}
				else if (font.isSpecialBreakCharacter(c)) //possible break
				{
					word.append(c);
					wW += cw;
					w += cw;
					if (w > width)
					{
						strs.add(str.toString());
						str = new StringBuilder();
						w = wW;
					}
					else
					{
						str.append(word);
						word = new StringBuilder();
						wW = 0;
					}
				}
				else //character
				{
					word.append(c);
					wW += cw;
					w += cw;
					if (w > width)
					{
						strs.add(str.toString());
						str = new StringBuilder();
						w = wW;
					}
				}
			}
			str.append(word);
			strs.add(str.toString());
			List<String> processed = new ArrayList<>();
			for (String txt : strs) /*if (txt.length() > 0)*/ processed.add(txt);
			text.text = String.join("\n", processed);
			text = text.appended;
		}
		return this;
	}
	
	public int getWidth()
	{
		return checkWidth(0, 0);
	}
	
	protected int checkWidth(int maxWidth, int curWidth)
	{
		for (int i = 0; i < text.length(); i++)
		{
			char c = text.charAt(i);
			if (c == '\n')
			{
				if (curWidth > maxWidth) maxWidth = curWidth;
				curWidth = 0;
			}
			else curWidth += font.widths[(int) c];
		}
		if (appended != null) return appended.checkWidth(maxWidth, curWidth);
		else return Math.max(curWidth, maxWidth);
	}
	
	public int getHeight()
	{
		return checkHeight(0, 0);
	}
	
	protected int checkHeight(int maxHeight, int curHeight)
	{
		if (curHeight < font.height && curHeight < font.height) curHeight = font.height;
		int numLines = text.split("\n").length;
		if (numLines > 0) //more than one line
		{
			maxHeight += curHeight;
			curHeight = font.height;
			if (numLines > 1) //more than two lines
			{
				maxHeight += curHeight * (numLines - 2);
			}
		}
		if (appended != null) return appended.checkHeight(maxHeight, curHeight);
		else return maxHeight + curHeight;
	}
	
	public int getNumLines()
	{
		return getNumNewLines() + 1;
	}
	
	private int getNumNewLines()
	{
		int count = (int) text.chars().filter(ch -> ch == '\n').count();
		if (this.appended != null) count += appended.getNumNewLines();
		return count;
	}
	
	public String getPlainText()
	{
		StringBuilder str = new StringBuilder();
		FormattedText text = this;
		while (text != null)
		{
			str.append(text.text);
			text = text.appended;
		}
		return str.toString();
	}
}