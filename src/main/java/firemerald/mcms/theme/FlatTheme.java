package firemerald.mcms.theme;

import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryUtil;

import firemerald.mcms.api.data.AbstractElement;
import firemerald.mcms.texture.Color;

public class FlatTheme extends BasicTheme
{
	public FlatTheme(String name, String origin, AbstractElement root)
	{
		super(name, origin, root);
	}

	@Override
	public void genRoundedBox(int w, int h, int outline, int radius, int tex)
	{
		if (radius <= 0) super.genRoundedBox(w, h, outline, radius, tex);
		else
		{
			ByteBuffer data = MemoryUtil.memAlloc(w * h * 4);
			setRegionDR(0, 0, radius, outline, this.outline, this.fill, CoordinateRotator._0, w, h, data);
			setRegionDL(w - radius, 0, radius, outline, this.outline, this.fill, CoordinateRotator._0, w, h, data);
			setRegionUL(w - radius, h - radius, radius, outline, this.outline, this.fill, CoordinateRotator._0, w, h, data);
			setRegionUR(0, h - radius, radius, outline, this.outline, this.fill, CoordinateRotator._0, w, h, data);
			setRegion(radius, 0, w - radius, outline, outlineI, CoordinateRotator._0, w, h, data);
			setRegion(radius, h - outline, w - radius, h, outlineI, CoordinateRotator._0, w, h, data);
			setRegion(0, radius, outline, h - radius, outlineI, CoordinateRotator._0, w, h, data);
			setRegion(w - outline, radius, w, h - radius, outlineI, CoordinateRotator._0, w, h, data);
			setRegion(radius, outline, w - radius, radius, fillI, CoordinateRotator._0, w, h, data);
			setRegion(outline, radius, w - outline, h - radius, fillI, CoordinateRotator._0, w, h, data);
			setRegion(radius, h - radius, w - radius, h - outline, fillI, CoordinateRotator._0, w, h, data);
			GuiTheme.makeTexture(tex, data, w, h);
			MemoryUtil.memFree(data);
		}
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
	
	@Override
	public void genTab(int w, int h, int outline, int radius, EnumDirection direction, boolean connectLeft, boolean connectRight, int tex)
	{
		if (radius <= 0) super.genTab(w, h, outline, radius, direction, connectLeft, connectRight, tex);
		else
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
			setRegionDR(radius, radius, radius, outline, this.outline, this.fill, CoordinateRotator._0, w, h, data);
			setRegionDL(vW - radius * 2, radius, radius, outline, this.outline, this.fill, CoordinateRotator._0, w, h, data);
			setRegion(radius * 2, radius, vW - radius * 2, radius + outline, outlineI, rotator, vW, vH, data);
			setRegion(radius * 2, radius + outline, vW - radius * 2, radius * 2, fillI, rotator, vW, vH, data);
			setRegion(radius + outline, radius * 2, vW - radius - outline, vH, fillI, rotator, vW, vH, data);
			if (connectLeft)
			{
				setRegionUL(0, vH - radius * 2, radius + outline, outline, this.outline, new Color(this.outline.c, 0), this.fill, CoordinateRotator._0, w, h, data);
				setRegion(radius, radius * 2, radius + outline, vH - radius * 2, outlineI, rotator, vW, vH, data);
				setRegion(0, vH - radius + outline, radius + outline, vH, fillI, rotator, vW, vH, data);
			}
			else
			{
				setRegion(radius, radius * 2, radius + outline, vH, outlineI, rotator, vW, vH, data);
			}
			if (connectRight)
			{
				setRegionUR(vW - radius - outline, vH - radius * 2, radius + outline, outline, this.outline, new Color(this.outline.c, 0), this.fill, CoordinateRotator._0, w, h, data);
				setRegion(vW - radius - outline, radius * 2, vW - radius, vH - radius * 2, outlineI, rotator, vW, vH, data);
				setRegion(vW - radius - outline, vH - radius + outline, vW, vH, fillI, rotator, vW, vH, data);
			}
			else
			{
				setRegion(vW - radius - outline, radius * 2, vW - radius, vH, outlineI, rotator, vW, vH, data);
			}
			GuiTheme.makeTexture(tex, data, w, h);
	    	MemoryUtil.memFree(data);
		}
	}
}