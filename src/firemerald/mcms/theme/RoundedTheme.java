package firemerald.mcms.theme;

import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryUtil;

import firemerald.mcms.api.data.Element;
import firemerald.mcms.texture.Color;

public class RoundedTheme extends BasicTheme
{
	public RoundedTheme(String name, String origin, Element root)
	{
		super(name, origin, root);
	}

	@Override
	public void generateRoundedBox(RoundedBoxFormat box)
	{
		int w = box.w;
		int h = box.h;
		int outline = box.outline;
		ByteBuffer data = MemoryUtil.memAlloc(w * h * 4);
		setBaseRectangle(w, h, outline, box.radius, this.outline, this.fill, data);
		int t = GuiTheme.makeTexture(data, w, h);
		boxes.put(box, t);
    	MemoryUtil.memFree(data);
	}
	
	public void setBaseRectangle(int w, int h, int outline, int radius, Color outlineC, Color innerC, ByteBuffer data)
	{
		int outlineI = outlineC.toARGB();
		int innerI = innerC.toARGB();
		if (radius <= 0) setBaseRectangle(w, h, outline, outlineI, innerI, data);
		else
		{
			int rI = radius - outline;
	    	int ind = 0;
	    	int ry = radius;
	    	for (int y = 0; y < (h / 2) && y < radius; y++) //top round
	    	{
	    		boolean isOutline = (y < outline) || (y >= (h - outline));
	        	int rx = radius;
	    		for (int x = 0; x < (w / 2) && x < radius; x++) //top-left round
	    		{
	    			data.putInt(ind, getColor(x, y, rx, ry, radius, rI, outlineC, innerC).toARGB());
	    			ind += 4;
	    		}
	    		
	    		for (int x = radius; x < (w - radius); x++) //top
	    		{
	    			data.putInt(ind, isOutline || (x < outline) || (x >= (w - outline)) ? outlineI : innerI);
	    			ind += 4;
	    		}

	        	rx = w - radius - 1;
	    		for (int x = radius > (w / 2) ? w / 2 : w - radius; x < w; x++) //top-right round
	    		{
	    			data.putInt(ind, getColor(x, y, rx, ry, radius, rI, outlineC, innerC).toARGB());
	    			ind += 4;
	    		}
	    	}

	    	for (int y = radius; y < (h - radius); y++)
	    	{
	    		boolean isOutline = (y < outline) || (y >= (h - outline));
	    		for (int x = 0; x < w; x++)
	    		{
	    			data.putInt(ind, isOutline || (x < outline) || (x >= (w - outline)) ? outlineI : innerI);
	    			ind += 4;
	    		}
	    	}

	    	ry = h - radius - 1;
	    	for (int y = radius > (h / 2) ? (h / 2) : h - radius; y < h; y++) //top round
	    	{
	    		boolean isOutline = (y < outline) || (y >= (h - outline));
	        	int rx = radius;
	    		for (int x = 0; x < (w / 2) && x < radius; x++) //top-left round
	    		{
	    			data.putInt(ind, getColor(x, y, rx, ry, radius, rI, outlineC, innerC).toARGB());
	    			ind += 4;
	    		}
	    		
	    		for (int x = radius; x < (w - radius); x++) //top
	    		{
	    			data.putInt(ind, isOutline || (x < outline) || (x >= (w - outline)) ? outlineI : innerI);
	    			ind += 4;
	    		}

	        	rx = w - radius - 1;
	    		for (int x = radius > (w / 2) ? w / 2 : w - radius; x < w; x++) //top-right round
	    		{
	    			data.putInt(ind, getColor(x, y, rx, ry, radius, rI, outlineC, innerC).toARGB());
	    			ind += 4;
	    		}
	    	}
		}
	}
	
	public Color getColor(int x, int y, int rx, int ry, int rO, int rI, Color outlineC, Color innerC)
	{
		int dy = y - ry;
		int dx = x - rx;
		float r = (float) Math.sqrt(dx * dx + dy * dy);
		float rdI;
		Color c;
		if ((rdI = (r - rI)) > 0)
		{
			if (rdI >= 1)
			{
				c = new Color(outlineC);
			}
			else
			{
				c = Color.mix(innerC, outlineC, rdI);
			}
		}
		else c = new Color(innerC);
		float rdO;
		if ((rdO = (r - rO)) > 0)
		{
			if (rdO < 1) c.a *= (1 - rdO);
			else c.a = 0;
		}
		return c;
	}
}