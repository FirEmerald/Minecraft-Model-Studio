package firemerald.mcms.theme;

import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryUtil;

import firemerald.mcms.api.data.AbstractElement;

public class FlatTheme extends BasicTheme
{
	public FlatTheme(String name, String origin, AbstractElement root)
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
		setBaseRectangle(w, h, outline, box.radius, this.outlineI, this.fillI, data);
		int t = GuiTheme.makeTexture(data, w, h);
		boxes.put(box, t);
    	MemoryUtil.memFree(data);
	}
	
	public void setBaseRectangle(int w, int h, int outline, int radius, int outlineI, int innerI, ByteBuffer data)
	{
		if (radius <= 0) setBaseRectangle(w, h, outline, outlineI, innerI, data);
		else
		{
			int empty = outlineI & 0x00FFFFFF;
	    	int ind = 0;
	    	for (int y = 0; y < (h / 2) && y < radius; y++) //top round
	    	{
	    		boolean isOutline = (y < outline) || (y >= (h - outline));
	    		for (int x = 0; x < (w / 2) && x < radius; x++) //top-left round
	    		{
	    			int tx = x + y - radius + 1;
	    			data.putInt(ind, tx < 0 ? empty : tx < outline ? outlineI : innerI);
	    			ind += 4;
	    		}
	    		
	    		for (int x = radius; x < (w - radius); x++) //top
	    		{
	    			data.putInt(ind, isOutline || (x < outline) || (x >= (w - outline)) ? outlineI : innerI);
	    			ind += 4;
	    		}

	    		for (int x = radius > (w / 2) ? w / 2 : w - radius; x < w; x++) //top-right round
	    		{
	    			int tx = x - w + radius - y - 1;
	    			data.putInt(ind, tx < -outline ? innerI : tx < 0 ? outlineI : empty);
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

	    	for (int y = radius > (h / 2) ? (h / 2) : h - radius; y < h; y++) //top round
	    	{
	    		boolean isOutline = (y < outline) || (y >= (h - outline));
	    		for (int x = 0; x < (w / 2) && x < radius; x++) //top-left round
	    		{
	    			int tx = x - y + h - radius;
	    			data.putInt(ind, tx < 0 ? empty : tx < outline ? outlineI : innerI);
	    			ind += 4;
	    		}
	    		
	    		for (int x = radius; x < (w - radius); x++) //top
	    		{
	    			data.putInt(ind, isOutline || (x < outline) || (x >= (w - outline)) ? outlineI : innerI);
	    			ind += 4;
	    		}

	    		for (int x = radius > (w / 2) ? w / 2 : w - radius; x < w; x++) //top-right round
	    		{
	    			int tx = x - w + y - h + radius;
	    			data.putInt(ind, tx < -outline ? innerI : tx < 0 ? outlineI : empty);
	    			ind += 4;
	    		}
	    	}
		}
	}
}