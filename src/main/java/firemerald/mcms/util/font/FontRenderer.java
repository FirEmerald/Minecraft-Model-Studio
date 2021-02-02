package firemerald.mcms.util.font;

import static org.lwjgl.opengl.GL11.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL15;

import firemerald.mcms.Main;
import firemerald.mcms.api.util.FileUtil;
import firemerald.mcms.shader.GuiShader;
import firemerald.mcms.texture.Color;
import firemerald.mcms.texture.RGB;
import firemerald.mcms.util.ResourceLocation;
import firemerald.mcms.util.TextureManager;
import firemerald.mcms.util.mesh.DrawMode;
import firemerald.mcms.util.mesh.GuiMesh;

public class FontRenderer
{
	public static final Map<ResourceLocation, FontRenderer> FONTS = new HashMap<>();
	public static final GuiMesh RECT = new GuiMesh(new float[] {
			0, 0,
			1, 0,
			1, 1,
			0, 1
	}, new float[] {
			1, 0,
			1, 0,
			1, 1,
			0, 1
	}, new int[] {
			0, 3, 1,
			1, 3, 2
	}, DrawMode.TRIANGLES, GL15.GL_DYNAMIC_DRAW);
	
	public final byte[] widths = new byte[65536];
	public final int height, start, end;
	public final ResourceLocation name, texLoc;
	public final GuiMesh charMesh, italicMesh;
	public final boolean linear;
	private final ResourceLocation[] pageTexs = new ResourceLocation[256];
	
	public FontRenderer(ResourceLocation font, int height)
	{
		this(font, height, 0, 65535, false);
	}
	
	public FontRenderer(ResourceLocation font, int height, boolean linear)
	{
		this(font, height, 0, 65535, linear);
	}
	
	public FontRenderer(ResourceLocation font, int height, int start, int end)
	{
		this(font, height, start, end, false);
	}
	
	public FontRenderer(ResourceLocation font, int height, int start, int end, boolean linear)
	{
		this.name = font;
		this.height = height;
		this.start = start;
		this.end = end;
		this.texLoc = font.prependPath("fonts/");
		try
		{
			InputStream in = Main.getResource(texLoc.prependPath("textures/").appendPath("/widths.bin"));
			in.read(widths);
			FileUtil.closeSafe(in);
		}
		catch (Exception e) {}
		for (int i = 0; i < start; i++) widths[i] = 0;
		for (int i = end; i < 65536; i++) widths[i] = 0;
		final int n = -height / 2, p = 3 * height / 2;
		charMesh = new GuiMesh(n, n, p, p, 0, 0, .0625f, .0625f);
		italicMesh = new GuiMesh(n + height / 4, n, 0, 0, p + height / 4, n, .0625f, 0, p - height / 4, p, .0625f, .0625f,	n - height / 4, p, 0, .0625f);
		FONTS.put(font, this);
		this.linear = linear;
	}
	
	public int getSelectedPos(String s, int x)
	{
		int w = 0;
		char[] chrs = s.toCharArray();
		int pos;
		int prevHalf = 0;
		for (pos = 0; pos < chrs.length; pos++)
		{
			int width = widths[chrs[pos]];
			int half = width >> 1;
			if ((w += prevHalf + half) > x) break;
			prevHalf = width - half;
		}
		return pos;
	}
	
	public int getStringWidth(String s)
	{
		int cW = 0;
		int mW = 0;
		for (int i = 0; i < s.length(); i++)
		{
			char c = s.charAt(i);
			if (c == '\n')
			{
				if (cW > mW) mW = cW;
				cW = 0;
			}
			else cW += widths[Character.valueOf(c)];
		}
		if (cW > mW) mW = cW;
		return mW;
	}
	
	public void drawText(String s, float x, float y, float r, float g, float b, float a)
	{
		drawText(s, x, y, r, g, b, a, false, false, false);
	}
	
	public void drawText(String s, float x, float y, float r, float g, float b, float a, boolean bold, boolean italic, boolean shadow)
	{
		float nx = x;
		for (int i = 0; i < s.length(); i++)
		{
			char c = s.charAt(i);
			if (c == '\n')
			{
				x = nx;
				y += height;
			}
			else
			{
				this.drawChar(c, x, y, r, g, b, a, bold, italic, shadow, false, false);
				x += widths[Character.valueOf(c)];
			}
		}
	}
	
	public String[] splitForWidth(String s, float width)
	{
		int w = 0, wW = 0;
		StringBuilder str = new StringBuilder();
		StringBuilder word = new StringBuilder();
		List<String> strs = new ArrayList<String>();
		for (int pos = 0; pos < s.length(); pos++)
		{
			char c = s.charAt(pos);
			if (c == '\n') //new line
			{
				str.append(word);
				str.append(c);
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
				w += widths[c];
				wW = 0;
			}
			else if (isSpecialBreakCharacter(c)) //possible break
			{
				word.append(c);
				wW += widths[c];
				w += widths[c];
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
				wW += widths[c];
				w += widths[c];
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
		for (String text : strs) if (text.length() > 0) processed.add(text);
		return processed.toArray(new String[processed.size()]);
	}
	
	public boolean isSpecialBreakCharacter(char c)
	{
		return c == '\\' || c == '/' || c == '.' || c == ','; 
	}
	
	public void drawTextLine(String s, float x, float y, Color color)
	{
		RGB rgb = color.c.getRGB();
		drawTextLine(s, x, y, rgb.r, rgb.g, rgb.b, color.a);
	}
	
	public void drawTextLine(String s, float x, float y, float r, float g, float b, float a)
	{
		drawTextLine(s, x, y, r, g, b, a, false, false, false);
	}
	
	public void drawTextLine(String s, float x, float y, Color color, boolean bold, boolean italic, boolean shadow)
	{
		RGB rgb = color.c.getRGB();
		drawTextLine(s, x, y, rgb.r, rgb.g, rgb.b, color.a, bold, italic, shadow);
	}
	
	public void drawTextLine(String s, float x, float y, float r, float g, float b, float a, boolean bold, boolean italic, boolean shadow)
	{
		for (int i = 0; i < s.length(); i++)
		{
			char c = s.charAt(i);
			this.drawChar(c, x, y, r, g, b, a, bold, italic, shadow, false, false);
			x += widths[Character.valueOf(c)];
		}
		Main.instance.guiShader.setTexOffset(0, 0);
	}
	
	public void drawTextLineCentered(String s, float x, float y, float r, float g, float b, float a)
	{
		drawTextLineCentered(s, x, y, r, g, b, a, false, false, false, false, false);
	}
	
	public void drawTextLineCentered(String s, float x, float y, float r, float g, float b, float a, boolean bold, boolean italic, boolean shadow, boolean underline, boolean strikethrough)
	{
		x = x - (getStringWidth(s) >> 1);
		for (int i = 0; i < s.length(); i++)
		{
			char c = s.charAt(i);
			drawChar(c, x, y, r, g, b, a, bold, italic, shadow, underline, strikethrough);
			x += widths[Character.valueOf(c)];
		}
		Main.instance.guiShader.setTexOffset(0, 0);
	}

	public void drawTextLineCentered(String s, float x, float y, Color color)
	{
		drawTextLineCentered(s, x, y, color, false, false, false, false, false);
	}

	public void drawTextLineCentered(String s, float x, float y, Color color, boolean bold, boolean italic, boolean shadow, boolean underline, boolean strikethrough)
	{
		RGB rgb = color.c.getRGB();
		drawTextLineCentered(s, x, y, rgb.r, rgb.g, rgb.b, color.a, bold, italic, shadow, underline, strikethrough);
	}
	
	private void bindPage(TextureManager texs, int page)
	{
		ResourceLocation tex = this.pageTexs[page];
		if (tex == null) this.pageTexs[page] = tex = texLoc.appendPath("/" + nameOfMeta(page) + ".png");
		texs.bindTexture(tex);
	}
	
	private void drawChar(char c, float x, float y, float r, float g, float b, float a, boolean bold, boolean italic, boolean shadow, boolean underline, boolean strikethrough)
	{
		int w = this.widths[c];
		int ind = Character.valueOf(c);
		if (ind < start || ind > end) return;
		int page = (ind >> 8) & 255;
		float oX = (ind & 15) * .0625f;
		float oY = ((ind >> 4) & 15) * .0625f;
		GuiShader s = Main.instance.guiShader;
		s.setTexOffset(oX, oY);
		TextureManager texs = Main.instance.textureManager;
		bindPage(texs, page);
		if (linear)
		{
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		}
		else
		{
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		}
		GuiMesh mesh = italic ? italicMesh : charMesh;
		GuiShader.MODEL.push();
		if (bold)
		{
			w++;
			if (shadow)
			{
				s.setColor(0, 0, 0, 1);
				GuiShader.MODEL.matrix().translate(x + 2, y + 1, 0); //x+2,y+1
				s.updateModel();
				mesh.render();
				GuiShader.MODEL.matrix().translate(0, 1, 0);//x+2,y+2
				s.updateModel();
				mesh.render();
				GuiShader.MODEL.matrix().translate(-1, 0, 0);//x+1,y+2
				s.updateModel();
				mesh.render();
				GuiShader.MODEL.matrix().translate(0, -1, 0);//x+1,y+1
				if (underline)
				{
					setImage(0, height, w, height + 1);
					texs.unbindTexture();
					RECT.render();
				}
				if (strikethrough)
				{
					setImage(0, height * 3 / 4, w, height * 3 / 4 + 1);
					texs.unbindTexture();
					RECT.render();
				}
				bindPage(texs, page);
				GuiShader.MODEL.matrix().translate(-1, -1, 0);//x,y
			}
			else GuiShader.MODEL.matrix().translate(x, y, 0);//x,y
			s.setColor(r, g, b, a);
			s.updateModel();
			mesh.render();
			GuiShader.MODEL.matrix().translate(1, 0, 0);//x+1,y
			s.updateModel();
			mesh.render();
			GuiShader.MODEL.matrix().translate(0, 1, 0);//x+1,y+1
			s.updateModel();
			mesh.render();
			GuiShader.MODEL.matrix().translate(-1, 0, 0);//x,y+1
			s.updateModel();
			mesh.render();
			GuiShader.MODEL.matrix().translate(0, -1, 0);//x,y
		}
		else
		{
			if (shadow)
			{
				s.setColor(0, 0, 0, a);
				GuiShader.MODEL.matrix().translate(x + 1, y + 1, 0);//x+1,y+1
				s.updateModel();
				mesh.render();
				if (underline)
				{
					setImage(0, height, w, height + 1);
					texs.unbindTexture();
					RECT.render();
				}
				if (strikethrough)
				{
					setImage(0, height * 3 / 4, w, height * 3 / 4 + 1);
					texs.unbindTexture();
					RECT.render();
				}
				bindPage(texs, page);
				GuiShader.MODEL.matrix().translate(-1, -1, 0);//x,y
			}
			else GuiShader.MODEL.matrix().translate(x, y, 0);//x,y
			s.updateModel();
			s.setColor(r, g, b, a);
			mesh.render();
		}
		if (underline)
		{
			setImage(0, height, w, height + 1);
			texs.unbindTexture();
			RECT.render();
		}
		if (strikethrough)
		{
			setImage(0, height * 3 / 4, w, height * 3 / 4 + 1);
			texs.unbindTexture();
			RECT.render();
		}
		GuiShader.MODEL.pop();
		s.updateModel();
		if (linear)
		{
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		}
		s.setTexOffset(0, 0);
	}
	
	public static String nameOfMeta(int meta)
	{
		return String.format("%02x", meta);
	}
	
	public static void drawTextFormatted(String text, float x, float y, FontRenderer font, float r, float g, float b, float a)
	{
		drawTextFormatted(text, x, y, font, r, g, b, a, false);
	}
	
	public static void drawTextFormatted(String text, float x, float y, FontRenderer font, float r, float g, float b, float a, boolean shadow)
	{
		drawTextFormatted(text, x, y, font, r, g, b, a, shadow, true);
	}
	
	public static void drawTextFormatted(String text, float x, float y, FontRenderer font, float r, float g, float b, float a, boolean shadow, boolean allowFontChange)
	{
		boolean italic = false, bold = false, strikethrough = false, underline = false;
		GuiShader s = Main.instance.guiShader;
		int h = font.height;
		char c;
		int p = 0;
		float w = x;
		int length = text.length();
		while (p < length)
		{
			c = text.charAt(p);
			if (c == '\n')
			{
				y += h;
				h = font.height;
				w = x;
				p++;
			}
			else if (c == Formatting.CHAR_SELECTOR)
			{
				c = text.charAt(++p);
				p++;
				switch (c)
				{
				case Formatting.CHAR_COLOR:
					int col = Integer.parseInt(text.substring(p, p + 6), 16);
					r = ((col >> 16) & 255) / 255f;
					g = ((col >> 8) & 255) / 255f;
					b = (col & 255) / 255f;
					p += 6;
					break;
				case Formatting.CHAR_FONT:
					if (allowFontChange)
					{
						int end = text.indexOf(';', p);
						if (end != -1)
						{
							ResourceLocation fontname = new ResourceLocation(text.substring(p, end));
							p = end + 1;
							if (!font.name.equals(fontname) && FONTS.containsKey(fontname))
							{
								font = FONTS.get(fontname);
								if (font.height > h) h = font.height;
							}
						}
					}
					break;
				case Formatting.CHAR_UNDERLINE:
					underline = !underline;
					break;
				case Formatting.CHAR_STRIKETHROUGH:
					strikethrough = !strikethrough;
					break;
				case Formatting.CHAR_ITALIC:
					italic = !italic;
					break;
				case Formatting.CHAR_BOLD:
					bold = !bold;
					break;
				case Formatting.CHAR_RESET:
					underline = strikethrough = italic = bold = false;
					break;
				}
			}
			else
			{
				font.drawChar(c, w + 1, y + 1, r, g, b, a, bold, italic, shadow, underline, strikethrough);
				int width = font.widths[c];
				if (bold) width++;
				p++;
				w += width;
			}
		}
		s.setTexOffset(0, 0);
	}
	
	public static void drawTextFormatted(FormattedText formatted, float x, float y, boolean shadow, boolean allowFontChange)
	{
		GuiShader s = Main.instance.guiShader;
		float w = x;
		while (formatted != null)
		{
			String text = formatted.text;
			boolean bold = formatted.bold;
			boolean italic = formatted.italic;
			boolean underline = formatted.underline;
			boolean strikethrough = formatted.strikethrough;
			Color color = formatted.color;
			RGB rgb = color.c.getRGB();
			float r = rgb.r;
			float g = rgb.g;
			float b = rgb.b;
			float a = color.a;
			FontRenderer font = formatted.font;
			int h = font.height;
			char c;
			int p = 0;
			int length = text.length();
			while (p < length)
			{
				c = text.charAt(p);
				if (c == '\n')
				{
					y += h;
					h = font.height;
					w = x;
					p++;
				}
				else
				{
					font.drawChar(c, w + 1, y + 1, r, g, b, a, bold, italic, shadow, underline, strikethrough);
					int width = font.widths[c];
					if (bold) width++;
					p++;
					w += width;
				}
			}
			formatted = formatted.appended;
		}
		s.setTexOffset(0, 0);
	}
	
	public static int getTextWidthFormatted(String text, FontRenderer font, boolean bold, boolean allowFontChange)
	{
		char c;
		int p = 0;
		int w = 0;
		int length = text.length();
		int maxWidth = 0;
		while (p < length)
		{
			c = text.charAt(p);
			if (c == '\n')
			{
				if (maxWidth < w) maxWidth = w;
				w = 0;
				p++;
			}
			else if (c == Formatting.CHAR_SELECTOR)
			{
				c = text.charAt(++p);
				p++;
				switch (c)
				{
				case Formatting.CHAR_COLOR:
					p += 6;
					break;
				case Formatting.CHAR_FONT:
					if (allowFontChange)
					{
						int end = text.indexOf(';', p);
						if (end != -1)
						{
							ResourceLocation fontname = new ResourceLocation(text.substring(p, end));
							p = end + 1;
							if (!font.name.equals(fontname) && FONTS.containsKey(fontname))
							{
								font = FONTS.get(fontname);
							}
						}
					}
					break;
				case Formatting.CHAR_BOLD:
					bold = !bold;
					break;
				case Formatting.CHAR_RESET:
					bold = false;
					break;
				}
			}
			else
			{
				int width = font.widths[c];
				if (bold) width++;
				p++;
				w += width;
			}
		}
		if (maxWidth < w) maxWidth = w;
		return maxWidth;
	}
	
	private static void setImage(float x1, float y1, float x2, float y2)
	{
		RECT.setPositions(new float[] {
				x1, y1, 0,
				x2, y1, 0,
				x2, y2, 0,
				x1, y2, 0
		});
	}
	
	public void drawTextFormatted(String text, float x, float y, float r, float g, float b, float a)
	{
		drawTextFormatted(text, x, y, r, g, b, a, false);
	}
	
	public void drawTextFormatted(String text, float x, float y, float r, float g, float b, float a, boolean shadow)
	{
		drawTextFormatted(text, x, y, this, r, g, b, a, shadow, false);
	}
}