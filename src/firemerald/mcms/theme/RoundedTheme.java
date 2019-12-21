package firemerald.mcms.theme;

import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryUtil;

import firemerald.mcms.api.data.AbstractElement;
import firemerald.mcms.texture.Color;

public class RoundedTheme extends BasicTheme
{
	public RoundedTheme(String name, String origin, AbstractElement root)
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
			setRegionCurved(radius, radius, 0, 0, radius, radius, radius, radius - outline, this.outline, this.fill, CoordinateRotator._0, w, h, data);
			setRegionCurved(w - radius, radius, w - radius, 0, w, radius, radius, radius - outline, this.outline, this.fill, CoordinateRotator._0, w, h, data);
			setRegionCurved(w - radius, h - radius, w - radius, h - radius, w, h, radius, radius - outline, this.outline, this.fill, CoordinateRotator._0, w, h, data);
			setRegionCurved(radius, h - radius, 0, h - radius, radius, h, radius, radius - outline, this.outline, this.fill, CoordinateRotator._0, w, h, data);
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
	
	public Color getColor(int x, int y, int rx, int ry, int rO, int rI, Color outlineC, Color innerC)
	{
		return getColor(x, y, rx, ry, rO, rI, outlineC, innerC, new Color(outlineC.c, 0));
	}
	
	public Color getColor(int x, int y, int rx, int ry, int rO, int rI, Color outlineC, Color innerC, Color backgroundC)
	{
		double dy = y - ry + .5f;
		double dx = x - rx + .5f;
		float r = (float) Math.sqrt(dx * dx + dy * dy) + .5f;
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
			if (rdO < 1) c = Color.mix(c, backgroundC, rdO);
			else c = backgroundC;
		}
		return c;
	}

	public void setRegionCurved(int cX, int cY, int x1, int y1, int x2, int y2, int rO, int rI, Color outlineC, Color innerC, CoordinateRotator rotation, int w, int h, ByteBuffer data)
	{
		setRegionCurved(cX, cY, x1, y1, x2, y2, rO, rI, outlineC, innerC, new Color(outlineC.c, 0), rotation, w, h, data);
	}
	
	public void setRegionCurved(int cX, int cY, int x1, int y1, int x2, int y2, int rO, int rI, Color outlineC, Color innerC, Color backgroundC, CoordinateRotator rotation, int w, int h, ByteBuffer data)
	{
		for (int y = y1; y < y2; y++) for (int x = x1; x < x2; x++) rotation.set(x, y, getColor(x, y, cX, cY, rO, rI, outlineC, innerC, backgroundC).toARGB(), data, w, h);
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
			setRegionCurved(radius * 2, radius * 2, radius, radius, radius * 2, radius * 2, radius, radius - outline, this.outline, this.fill, rotator, vW, vH, data);
			setRegionCurved(vW - radius * 2, radius * 2, vW - radius * 2, radius, vW - radius, radius * 2, radius, radius - outline, this.outline, this.fill, rotator, vW, vH, data);
			setRegion(radius * 2, radius, vW - radius * 2, radius + outline, outlineI, rotator, vW, vH, data);
			setRegion(radius * 2, radius + outline, vW - radius * 2, radius * 2, fillI, rotator, vW, vH, data);
			setRegion(radius + outline, radius * 2, vW - radius - outline, vH, fillI, rotator, vW, vH, data);
			if (connectLeft)
			{
				setRegionCurved(0, vH - radius * 2, 0, vH - radius * 2, radius + outline, vH - radius + outline, radius + outline, radius, this.outline, new Color(this.outline.c, 0), this.fill, rotator, vW, vH, data);
				setRegion(radius, radius * 2, radius + outline, vH - radius * 2, outlineI, rotator, vW, vH, data);
				setRegion(0, vH - radius + outline, radius + outline, vH, fillI, rotator, vW, vH, data);
			}
			else
			{
				setRegion(radius, radius * 2, radius + outline, vH, outlineI, rotator, vW, vH, data);
			}
			if (connectRight)
			{
				setRegionCurved(vW, vH - radius * 2, vW - radius - outline, vH - radius * 2, vW, vH - radius + outline, radius + outline, radius, this.outline, new Color(this.outline.c, 0), this.fill, rotator, vW, vH, data);
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