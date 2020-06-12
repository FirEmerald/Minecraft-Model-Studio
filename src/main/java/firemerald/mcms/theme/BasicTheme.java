package firemerald.mcms.theme;

import static org.lwjgl.opengl.GL11.*;

import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryUtil;

import firemerald.mcms.api.data.AbstractElement;
import firemerald.mcms.texture.Color;
import firemerald.mcms.texture.RGB;
import firemerald.mcms.util.MathUtil;
import firemerald.mcms.util.MiscUtil;

public class BasicTheme extends CommonGuiTheme
{
	protected Color background = Color.DARK_GREY, outline = Color.BLACK, fill = Color.WHITE, textbox = Color.LIGHT_GREY, text = Color.BLACK, scrollbar = Color.LIGHT_GREY;
	public final int outlineI, fillI, textboxI, scrollbarI;
	
	public BasicTheme(String name, String origin, AbstractElement root)
	{
		super(name, origin);
		for (AbstractElement el : root.getChildren())
		{
			String elName = el.getName();
			if (elName.equalsIgnoreCase("background")) background = MiscUtil.getColor(el, background);
			else if (elName.equalsIgnoreCase("outline")) outline = MiscUtil.getColor(el, outline);
			else if (elName.equalsIgnoreCase("fill")) fill = MiscUtil.getColor(el, fill);
			else if (elName.equalsIgnoreCase("textbox")) textbox = MiscUtil.getColor(el, textbox);
			else if (elName.equalsIgnoreCase("scrollbar")) scrollbar = MiscUtil.getColor(el, scrollbar);
			else if (elName.equalsIgnoreCase("text")) text = MiscUtil.getColor(el, text);
		}
		outlineI = outline.toARGB();
		fillI = fill.toARGB();
		textboxI = textbox.toARGB();
		scrollbarI = scrollbar.toARGB();
	}
	
	public BasicTheme()
	{
		super("default", "");
		outlineI = outline.toARGB();
		fillI = fill.toARGB();
		textboxI = textbox.toARGB();
		scrollbarI = scrollbar.toARGB();
	}

	@Override
	public void drawBackground()
	{
		RGB rgb = background.c.getRGB();
		glClearColor(rgb.r, rgb.g, rgb.b, background.a);
		glClear(GL_COLOR_BUFFER_BIT);
	}

	@Override
	public Color getTextColor()
	{
		return text;
	}

	@Override
	public Color getFillColor()
	{
		return fill;
	}

	@Override
	public Color getOutlineColor()
	{
		return outline;
	}

	@Override
	public void genRoundedBox(int w, int h, int outline, int radius, int tex)
	{
		ByteBuffer data = MemoryUtil.memAlloc(w * h * 4);
		setBaseRectangle(w, h, outline, outlineI, fillI, data);
		GuiTheme.makeTexture(tex, data, w, h);
    	MemoryUtil.memFree(data);
	}

	@Override
	public void genTextBox(int w, int h, int outline, int tex)
	{
		ByteBuffer data = MemoryUtil.memAlloc(w * h * 4);
		setBaseRectangle(w, h, outline, outlineI, textboxI, data);
		GuiTheme.makeTexture(tex, data, w, h);
    	MemoryUtil.memFree(data);
	}

	@Override
	public void genScrollBar(int w, int h, int outline, int tex)
	{
		ByteBuffer data = MemoryUtil.memAlloc(w * h * 4);
		setBaseRectangle(w, h, outline, outlineI, scrollbarI, data);
		GuiTheme.makeTexture(tex, data, w, h);
    	MemoryUtil.memFree(data);
	}

	@Override
	public void genScrollButton(int w, int h, int outline, EnumDirection direction, int tex)
	{
		ByteBuffer data = MemoryUtil.memAlloc(w * h * 4);
		setBaseRectangle(w, h, outline, outlineI, fillI, data);
		addScrollArrows(w, h, outlineI, direction, data);
		GuiTheme.makeTexture(tex, data, w, h);
    	MemoryUtil.memFree(data);
	}

	@Override
	public void genDirectionButton(int w, int h, int outline, int radius, EnumDirection direction, int tex)
	{
		ByteBuffer data = MemoryUtil.memAlloc(w * h * 4);
		setBaseRectangle(w, h, outline, outlineI, fillI, data);
		this.addDirectionArrows(w, h, outlineI, direction, data);
		GuiTheme.makeTexture(tex, data, w, h);
    	MemoryUtil.memFree(data);
	}

	@Override
	public void genArrowedButton(int w, int h, int outline, int radius, float x1, float y1, float x2, float y2, EnumDirection direction, int tex)
	{
		ByteBuffer data = MemoryUtil.memAlloc(w * h * 4);
		setBaseRectangle(w, h, outline, outlineI, fillI, data);
		this.addDirectionArrows(w, h, outlineI, x1, y1, x2, y2, direction, data);
		GuiTheme.makeTexture(tex, data, w, h);
    	MemoryUtil.memFree(data);
	}

	@Override
	public void genArrow(int h, int outline, EnumDirection direction, int tex)
	{
		int w = h * 2;
		ByteBuffer data = MemoryUtil.memAlloc(w * h * 4);
		CoordinateRotator rotator = CoordinateRotator.forDirection(direction);
		setRectangle(w, h, 0x00000000, data);
		this.setRegionUR(0, 0, h, outline, this.outline, fill, rotator, w, h, data);
		this.setRegionUL(h, 0, h, outline, this.outline, fill, rotator, w, h, data);
		if (outline > 0) this.setRegionRGB(0, 0, w, outline, this.outlineI, rotator, w, h, data);
		GuiTheme.makeTexture(tex, data, w, h);
    	MemoryUtil.memFree(data);
	}

	@Override
	public void genMenuSeperator(int w, int h, int thickness, int offset, int tex)
	{
		ByteBuffer data = MemoryUtil.memAlloc(w * h * 4);
		setRectangle(w, h, fillI, data);
		int y = (h - thickness) / 2;
		setRegion(offset, y, w - offset, y + thickness, outlineI, CoordinateRotator._0, w, h, data);
		GuiTheme.makeTexture(tex, data, w, h);
    	MemoryUtil.memFree(data);
	}
	
	@Override
	public void genTab(int w, int h, int outline, int radius, EnumDirection direction, boolean connectLeft, boolean connectRight, int tex)
	{
		w += radius * 2;
		h += radius * 2;
		ByteBuffer data = MemoryUtil.memAlloc(w * h * 4);
		setRectangle(w, h, 0x00000000, data);
		int vW, vH;
		if (direction == EnumDirection.DOWN || direction == EnumDirection.UP)
		{
			vW = w;
			vH = h;
		}
		else
		{
			vW = h;
			vH = w;
		}
		CoordinateRotator rotator = CoordinateRotator.forDirection(direction);
		setRegion(radius, radius, vW - radius, radius + outline, outlineI, rotator, vW, vH, data);
		setRegion(radius + outline, radius + outline, vW - radius - outline, vH, fillI, rotator, vW, vH, data);
		if (connectLeft)
		{
			setRegion(radius, radius + outline, radius + outline, vH - radius, outlineI, rotator, vW, vH, data);
			setRegion(0, vH - radius, radius + outline, vH - radius + outline, outlineI, rotator, vW, vH, data);
			setRegion(0, vH - radius + outline, radius + outline, vH, fillI, rotator, vW, vH, data);
		}
		else
		{
			setRegion(radius, radius + outline, radius + outline, vH, outlineI, rotator, vW, vH, data);
		}
		if (connectRight)
		{
			setRegion(vW - radius - outline, radius + outline, vW - radius, vH - radius, outlineI, rotator, vW, vH, data);
			setRegion(vW - radius - outline, vH - radius, vW, vH - radius + outline, outlineI, rotator, vW, vH, data);
			setRegion(vW - radius - outline, vH - radius + outline, vW, vH, fillI, rotator, vW, vH, data);
		}
		else
		{
			setRegion(vW - radius - outline, radius + outline, vW - radius, vH, outlineI, rotator, vW, vH, data);
		}
		GuiTheme.makeTexture(tex, data, w, h);
    	MemoryUtil.memFree(data);
	}
	
	public void setBaseRectangle(int w, int h, int outline, int outlineI, int innerI, ByteBuffer data)
	{
		setRegion(0, 0, w, outline, outlineI, CoordinateRotator._0, w, h, data);
		setRegion(0, h - outline, w, h, outlineI, CoordinateRotator._0, w, h, data);
		setRegion(0, outline, outline, h - outline, outlineI, CoordinateRotator._0, w, h, data);
		setRegion(w - outline, outline, w, h - outline, outlineI, CoordinateRotator._0, w, h, data);
		setRegion(outline, outline, w - outline, h - outline, innerI, CoordinateRotator._0, w, h, data);
	}
	
	public void setRectangle(int w, int h, int color, ByteBuffer data)
	{
		setRegion(0, 0, w, h, color, CoordinateRotator._0, w, h, data);
	}
	
	public void setRegion(int x1, int y1, int x2, int y2, int color, CoordinateRotator rotation, int w, int h, ByteBuffer data)
	{
		for (int y = y1; y < y2; y++) for (int x = x1; x < x2; x++) rotation.set(x, y, color, data, w, h);
	}
	
	public void setRegionRGB(int x1, int y1, int x2, int y2, int rgb, CoordinateRotator rotation, int w, int h, ByteBuffer data)
	{
		for (int y = y1; y < y2; y++) for (int x = x1; x < x2; x++) rotation.set(x, y, (rotation.get(x, y, data, w, h) & 0xFF000000) | (rgb & 0xFFFFFF), data, w, h);
	}
	
	public void addArrowUp(float x1, float y1, float x2, float y2, int w, int h, int color, ByteBuffer data)
	{
		float x12 = (x1 + x2) * 0.5f;
		for (int ty = MathUtil.floor(y1); ty < MathUtil.ceil(y2); ty++)
		{
			float y = ty + 1;
			if (y > y2) y = y2;
			int startX = MathUtil.floor(x12 + (x1 - x12) * (y - y1) / (y2 - y1));
			int endX = MathUtil.ceil(x12 + (x2 - x12) * (y - y1) / (y2 - y1));
			int index = (ty * w + startX) * 4;
			for (int x = startX; x < endX; x++)
			{
				data.putInt(index, color);
				index += 4;
			}
		}
	}
	
	public void addArrowUpRight(float x1, float y1, float x2, float y2, int w, int h, int color, ByteBuffer data)
	{
		int endX = MathUtil.ceil(x2);
		for (int ty = MathUtil.floor(y1); ty < MathUtil.ceil(y2); ty++)
		{
			float y = ty;
			if (y < y1) y = y1;
			int startX = MathUtil.floor(x1 + (x2 - x1) * (y - y1) / (y2 - y1));
			int index = (ty * w + startX) * 4;
			for (int x = startX; x < endX; x++)
			{
				data.putInt(index, color);
				index += 4;
			}
		}
	}
	
	public void addArrowRight(float x1, float y1, float x2, float y2, int w, int h, int color, ByteBuffer data)
	{
		float y12 = (y1 + y2) * 0.5f;
		int startX = MathUtil.floor(x1);
		for (int ty = MathUtil.floor(y1); ty < MathUtil.ceil(y12); ty++)
		{
			float y = ty + 1;
			if (y > y12) y = y12;
			int endX = MathUtil.ceil(x1 + (x2 - x1) * (y - y1) / (y12 - y1));
			int index = (ty * w + startX) * 4;
			for (int x = startX; x < endX; x++)
			{
				data.putInt(index, color);
				index += 4;
			}
		}
		for (int ty = MathUtil.ceil(y12); ty < MathUtil.ceil(y2); ty++)
		{
			float y = ty;
			int endX = MathUtil.ceil(x2 + (x1 - x2) * (y - y12) / (y2 - y12));
			int index = (ty * w + startX) * 4;
			for (int x = startX; x < endX; x++)
			{
				data.putInt(index, color);
				index += 4;
			}
		}
	}
	
	public void addArrowDownRight(float x1, float y1, float x2, float y2, int w, int h, int color, ByteBuffer data)
	{
		int endX = MathUtil.ceil(x2);
		for (int ty = MathUtil.floor(y1); ty < MathUtil.ceil(y2); ty++)
		{
			float y = ty + 1;
			if (y > y2) y = y2;
			int startX = MathUtil.floor(x2 + (x1 - x2) * (y - y1) / (y2 - y1));
			int index = (ty * w + startX) * 4;
			for (int x = startX; x < endX; x++)
			{
				data.putInt(index, color);
				index += 4;
			}
		}
	}
	
	public void addArrowDown(float x1, float y1, float x2, float y2, int w, int h, int color, ByteBuffer data)
	{
		float x12 = (x1 + x2) * 0.5f;
		for (int ty = MathUtil.floor(y1); ty < MathUtil.ceil(y2); ty++)
		{
			float y = ty;
			if (y < y1) y = y1;
			int startX = MathUtil.floor(x1 + (x12 - x1) * (y - y1) / (y2 - y1));
			int endX = MathUtil.ceil(x2 + (x12 - x2) * (y - y1) / (y2 - y1));
			int index = (ty * w + startX) * 4;
			for (int x = startX; x < endX; x++)
			{
				data.putInt(index, color);
				index += 4;
			}
		}
	}
	
	public void addArrowDownLeft(float x1, float y1, float x2, float y2, int w, int h, int color, ByteBuffer data)
	{
		int startX = MathUtil.floor(x1);
		for (int ty = MathUtil.floor(y1); ty < MathUtil.ceil(y2); ty++)
		{
			float y = ty + 1;
			if (y > y2) y = y2;
			int endX = MathUtil.ceil(x1 + (x2 - x1) * (y - y1) / (y2 - y1));
			int index = (ty * w + startX) * 4;
			for (int x = startX; x < endX; x++)
			{
				data.putInt(index, color);
				index += 4;
			}
		}
	}
	
	public void addArrowLeft(float x1, float y1, float x2, float y2, int w, int h, int color, ByteBuffer data)
	{
		float y12 = (y1 + y2) * 0.5f;
		int endX = MathUtil.ceil(x2);
		for (int ty = MathUtil.floor(y1); ty < MathUtil.ceil(y12); ty++)
		{
			float y = ty + 1;
			if (y > y12) y = y12;
			int startX = MathUtil.floor(x2 + (x1 - x2) * (y - y1) / (y12 - y1));
			int index = (ty * w + startX) * 4;
			for (int x = startX; x < endX; x++)
			{
				data.putInt(index, color);
				index += 4;
			}
		}
		for (int ty = MathUtil.ceil(y12); ty < MathUtil.ceil(y2); ty++)
		{
			float y = ty;
			int startX = MathUtil.floor(x1 + (x2 - x1) * (y - y12) / (y2 - y12));
			int index = (ty * w + startX) * 4;
			for (int x = startX; x < endX; x++)
			{
				data.putInt(index, color);
				index += 4;
			}
		}
	}
	
	public void addArrowUpLeft(float x1, float y1, float x2, float y2, int w, int h, int color, ByteBuffer data)
	{
		int startX = MathUtil.floor(x1);
		for (int ty = MathUtil.floor(y1); ty < MathUtil.ceil(y2); ty++)
		{
			float y = ty;
			if (y < y1) y = y1;
			int endX = MathUtil.ceil(x2 + (x1 - x2) * (y - y1) / (y2 - y1));
			int index = (ty * w + startX) * 4;
			for (int x = startX; x < endX; x++)
			{
				data.putInt(index, color);
				index += 4;
			}
		}
	}
	
	public void addScrollArrows(int w, int h, int color, EnumDirection direction, ByteBuffer data)
	{
		float x0 = w * 2 / 10f, x1 = w / 2f, x2 = w * 8 / 10f;
		float y0 = h * 2 / 10f, y1 = h / 2f, y2 = h * 8 / 10f;
		switch (direction)
		{
		case UP:// /\
		{
			addArrowUp(x0, y0, x2, y1, w, h, color, data);
			addArrowUpRight(x1, y1, x2, y2, w, h, color, data);
			addArrowUpLeft(x0, y1, x1, y2, w, h, color, data);
			break;
		}
		case RIGHT:// >
		{
			addArrowRight(x1, y0, x2, y2, w, h, color, data);
			addArrowUpRight(x0, y0, x1, y1, w, h, color, data);
			addArrowDownRight(x0, y1, x1, y2, w, h, color, data);
			break;
		}
		case DOWN:// \/
		{
			addArrowDown(x0, y1, x2, y2, w, h, color, data);
			addArrowDownRight(x1, y0, x2, y1, w, h, color, data);
			addArrowDownLeft(x0, y0, x1, y1, w, h, color, data);
			break;
		}
		case LEFT:// <
		{
			addArrowLeft(x0, y0, x1, y2, w, h, color, data);
			addArrowDownLeft(x1, y1, x2, y2, w, h, color, data);
			addArrowUpLeft(x1, y0, x2, y1, w, h, color, data);
			break;
		}
		}
	}
	
	public void addDirectionArrows(int w, int h, int color, EnumDirection direction, ByteBuffer data)
	{
		float x0 = w * 2 / 10f, x2 = w * 8 / 10f;
		float y0 = h * 2 / 10f, y2 = h * 8 / 10f;
		addDirectionArrows(w, h, color, x0, y0, x2, y2, direction, data);
	}
	
	public void addDirectionArrows(int w, int h, int color, float x1, float y1, float x2, float y2, EnumDirection direction, ByteBuffer data)
	{
		switch (direction)
		{
		case UP:// /\
		{
			addArrowUp(x1, y1, x2, y2, w, h, color, data);
			break;
		}
		case RIGHT:// >
		{
			addArrowRight(x1, y1, x2, y2, w, h, color, data);
			break;
		}
		case DOWN:// \/
		{
			addArrowDown(x1, y1, x2, y2, w, h, color, data);
			break;
		}
		case LEFT:// <
		{
			addArrowLeft(x1, y1, x2, y2, w, h, color, data);
			break;
		}
		}
	}

	public void setRegionDR(int cX, int cY, int size, int outline, Color outlineC, Color innerC, CoordinateRotator rotation, int w, int h, ByteBuffer data)
	{
		setRegionDR(cX, cY, size, outline, outlineC, innerC, new Color(outlineC.c, 0), rotation, w, h, data);
	}

	public void setRegionDR(int cX, int cY, int size, int outline, Color outlineC, Color innerC, Color backgroundC, CoordinateRotator rotation, int w, int h, ByteBuffer data)
	{
		Color bo = Color.mix(outlineC, backgroundC, .5f);
		Color oi = Color.mix(outlineC, innerC, .5f);
		int offset = 0;
		for (int y = cY; y < cY + size; y++)
		{
			int t = 1 + offset - size;
			for (int x = cX; x < cX + size; x++)
			{
				Color c;
				if (t < -1) c = backgroundC;
				else if (t == -1) c = bo;
				else if (t < outline) c = outlineC;
				else if (t == outline) c = oi;
				else c = innerC;
				rotation.set(x, y, c.toARGB(), data, w, h);
				t++;
			}
			offset++;
		}
	}

	public void setRegionUR(int cX, int cY, int size, int outline, Color outlineC, Color innerC, CoordinateRotator rotation, int w, int h, ByteBuffer data)
	{
		setRegionUR(cX, cY, size, outline, outlineC, innerC, new Color(outlineC.c, 0), rotation, w, h, data);
	}

	public void setRegionUR(int cX, int cY, int size, int outline, Color outlineC, Color innerC, Color backgroundC, CoordinateRotator rotation, int w, int h, ByteBuffer data)
	{
		Color bo = Color.mix(outlineC, backgroundC, .5f);
		Color oi = Color.mix(outlineC, innerC, .5f);
		int offset = 0;
		for (int y = cY; y < cY + size; y++)
		{
			int t = -offset;
			for (int x = cX; x < cX + size; x++)
			{
				Color c;
				if (t < -1) c = backgroundC;
				else if (t == -1) c = bo;
				else if (t < outline) c = outlineC;
				else if (t == outline) c = oi;
				else c = innerC;
				rotation.set(x, y, c.toARGB(), data, w, h);
				t++;
			}
			offset++;
		}
	}

	public void setRegionUL(int cX, int cY, int size, int outline, Color outlineC, Color innerC, CoordinateRotator rotation, int w, int h, ByteBuffer data)
	{
		setRegionUL(cX, cY, size, outline, outlineC, innerC, new Color(outlineC.c, 0), rotation, w, h, data);
	}

	public void setRegionUL(int cX, int cY, int size, int outline, Color outlineC, Color innerC, Color backgroundC, CoordinateRotator rotation, int w, int h, ByteBuffer data)
	{
		Color bo = Color.mix(outlineC, backgroundC, .5f);
		Color oi = Color.mix(outlineC, innerC, .5f);
		int offset = 0;
		for (int y = cY; y < cY + size; y++)
		{
			int t = size - offset - 1;
			for (int x = cX; x < cX + size; x++)
			{
				Color c;
				if (t < -1) c = backgroundC;
				else if (t == -1) c = bo;
				else if (t < outline) c = outlineC;
				else if (t == outline) c = oi;
				else c = innerC;
				rotation.set(x, y, c.toARGB(), data, w, h);
				t--;
			}
			offset++;
		}
	}

	public void setRegionDL(int cX, int cY, int size, int outline, Color outlineC, Color innerC, CoordinateRotator rotation, int w, int h, ByteBuffer data)
	{
		setRegionDL(cX, cY, size, outline, outlineC, innerC, new Color(outlineC.c, 0), rotation, w, h, data);
	}

	public void setRegionDL(int cX, int cY, int size, int outline, Color outlineC, Color innerC, Color backgroundC, CoordinateRotator rotation, int w, int h, ByteBuffer data)
	{
		Color bo = Color.mix(outlineC, backgroundC, .5f);
		Color oi = Color.mix(outlineC, innerC, .5f);
		int offset = 0;
		for (int y = cY; y < cY + size; y++)
		{
			int t = offset;
			for (int x = cX; x < cX + size; x++)
			{
				Color c;
				if (t < -1) c = backgroundC;
				else if (t == -1) c = bo;
				else if (t < outline) c = outlineC;
				else if (t == outline) c = oi;
				else c = innerC;
				rotation.set(x, y, c.toARGB(), data, w, h);
				t--;
			}
			offset++;
		}
	}
	
	public static enum CoordinateRotator
	{
		_0() {
			@Override
			public void set(int x, int y, int color, ByteBuffer data, int w, int h)
			{
				data.putInt(((y * w) + x) * 4, color);
			}

			@Override
			public int get(int x, int y, ByteBuffer data, int w, int h)
			{
				return data.getInt(((y * w) + x) * 4);
			}
		},
		_90() {
			@Override
			public void set(int x, int y, int color, ByteBuffer data, int w, int h)
			{
				data.putInt((((w - x - 1) * h) + y) * 4, color);
			}

			@Override
			public int get(int x, int y, ByteBuffer data, int w, int h)
			{
				return data.getInt((((w - x - 1) * h) + y) * 4);
			}
		},
		_180() {
			@Override
			public void set(int x, int y, int color, ByteBuffer data, int w, int h)
			{
				data.putInt((((h - y - 1) * w) + w - x - 1) * 4, color);
			}

			@Override
			public int get(int x, int y, ByteBuffer data, int w, int h)
			{
				return data.getInt((((h - y - 1) * w) + w - x - 1) * 4);
			}
		},
		_270() {
			@Override
			public void set(int x, int y, int color, ByteBuffer data, int w, int h)
			{
				data.putInt(((x * h) + h - y - 1) * 4, color);
			}

			@Override
			public int get(int x, int y, ByteBuffer data, int w, int h)
			{
				return data.getInt(((x * h) + h - y - 1) * 4);
			}
		};
		
		public abstract void set(int x, int y, int color, ByteBuffer data, int w, int h);

		public abstract int get(int x, int y, ByteBuffer data, int w, int h);
		
		public static CoordinateRotator forDirection(EnumDirection direction)
		{
			switch (direction)
			{
			case RIGHT:
				return _90;
			case UP:
				return _180;
			case LEFT:
				return _270;
			case DOWN:
			default:
				return _0;
			}
		}
	}
}