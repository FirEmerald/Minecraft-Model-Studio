package firemerald.mcms.theme;

import static org.lwjgl.opengl.GL11.*;

import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryUtil;

import firemerald.mcms.api.data.Element;
import firemerald.mcms.texture.Color;
import firemerald.mcms.texture.RGB;
import firemerald.mcms.util.MathUtil;
import firemerald.mcms.util.MiscUtil;

public class BasicTheme extends CommonGuiTheme
{
	protected Color background = Color.DARK_GREY, outline = Color.BLACK, fill = Color.WHITE, textbox = Color.LIGHT_GREY, text = Color.BLACK, scrollbar = Color.LIGHT_GREY;
	public final int outlineI, fillI, textboxI, scrollbarI;
	
	public BasicTheme(String name, String origin, Element root)
	{
		super(name, origin);
		for (Element el : root.getChildren())
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
	public void generateRoundedBox(RoundedBoxFormat box)
	{
		int w = box.w;
		int h = box.h;
		int outline = box.outline;
		ByteBuffer data = MemoryUtil.memAlloc(w * h * 4);
		setBaseRectangle(w, h, outline, outlineI, fillI, data);
		int t = GuiTheme.makeTexture(data, w, h);
		boxes.put(box, t);
    	MemoryUtil.memFree(data);
	}

	@Override
	public void generateTextBox(BoxFormat textBox)
	{
		int w = textBox.w;
		int h = textBox.h;
		int outline = textBox.outline;
		ByteBuffer data = MemoryUtil.memAlloc(w * h * 4);
		setBaseRectangle(w, h, outline, outlineI, textboxI, data);
		int t = GuiTheme.makeTexture(data, w, h);
		textBoxes.put(textBox, t);
    	MemoryUtil.memFree(data);
	}

	@Override
	public void generateScrollBar(BoxFormat scrollBar)
	{
		int w = scrollBar.w;
		int h = scrollBar.h;
		int outline = scrollBar.outline;
		ByteBuffer data = MemoryUtil.memAlloc(w * h * 4);
		setBaseRectangle(w, h, outline, outlineI, scrollbarI, data);
		int t = GuiTheme.makeTexture(data, w, h);
		scrollBars.put(scrollBar, t);
    	MemoryUtil.memFree(data);
	}

	@Override
	public void generateScrollButton(DirectionButtonFormat scrollButton)
	{
		int w = scrollButton.w;
		int h = scrollButton.h;
		int outline = scrollButton.outline;
		ByteBuffer data = MemoryUtil.memAlloc(w * h * 4);
		setBaseRectangle(w, h, outline, outlineI, fillI, data);
		this.addScrollArrows(w, h, outlineI, scrollButton.direction, data);
		int t = GuiTheme.makeTexture(data, w, h);
		scrollButtons.put(scrollButton, t);
    	MemoryUtil.memFree(data);
	}

	@Override
	public void generateDirectionButton(DirectionButtonFormat directionButton)
	{
		int w = directionButton.w;
		int h = directionButton.h;
		int outline = directionButton.outline;
		ByteBuffer data = MemoryUtil.memAlloc(w * h * 4);
		setBaseRectangle(w, h, outline, outlineI, fillI, data);
		this.addDirectionArrows(w, h, outlineI, directionButton.direction, data);
		int t = GuiTheme.makeTexture(data, w, h);
		directionButtons.put(directionButton, t);
    	MemoryUtil.memFree(data);
	}
	
	public void setBaseRectangle(int w, int h, int outline, int outlineI, int innerI, ByteBuffer data)
	{
    	int ind = 0;
    	for (int y = 0; y < h; y++)
    	{
    		boolean isOutline = (y < outline) || (y >= (h - outline));
    		for (int x = 0; x < w; x++)
    		{
    			data.putInt(ind, isOutline || (x < outline) || (x >= (w - outline)) ? outlineI : innerI);
    			ind += 4;
    		}
    	}
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
	
	public void addScrollArrows(int w, int h, int color, int direction, ByteBuffer data)
	{
		float x0 = w * 2 / 10f, x1 = w / 2f, x2 = w * 8 / 10f;
		float y0 = h * 2 / 10f, y1 = h / 2f, y2 = h * 8 / 10f;
		switch (direction)
		{
		case 0:// /\
		{
			addArrowUp(x0, y0, x2, y1, w, h, color, data);
			addArrowUpRight(x1, y1, x2, y2, w, h, color, data);
			addArrowUpLeft(x0, y1, x1, y2, w, h, color, data);
			break;
		}
		case 1:// >
		{
			addArrowRight(x1, y0, x2, y2, w, h, color, data);
			addArrowUpRight(x0, y0, x1, y1, w, h, color, data);
			addArrowDownRight(x0, y1, x1, y2, w, h, color, data);
			break;
		}
		case 2:// \/
		{
			addArrowDown(x0, y1, x2, y2, w, h, color, data);
			addArrowDownRight(x1, y0, x2, y1, w, h, color, data);
			addArrowDownLeft(x0, y0, x1, y1, w, h, color, data);
			break;
		}
		case 3:// <
		{
			addArrowLeft(x0, y0, x1, y2, w, h, color, data);
			addArrowDownLeft(x1, y1, x2, y2, w, h, color, data);
			addArrowUpLeft(x1, y0, x2, y1, w, h, color, data);
			break;
		}
		}
	}
	
	public void addDirectionArrows(int w, int h, int color, int direction, ByteBuffer data)
	{
		float x0 = w * 2 / 10f, x2 = w * 8 / 10f;
		float y0 = h * 2 / 10f, y2 = h * 8 / 10f;
		switch (direction)
		{
		case 0:// /\
		{
			addArrowUp(x0, y0, x2, y2, w, h, color, data);
			break;
		}
		case 1:// >
		{
			addArrowRight(x0, y0, x2, y2, w, h, color, data);
			break;
		}
		case 2:// \/
		{
			addArrowDown(x0, y0, x2, y2, w, h, color, data);
			break;
		}
		case 3:// <
		{
			addArrowLeft(x0, y0, x2, y2, w, h, color, data);
			break;
		}
		}
	}
}