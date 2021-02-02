package firemerald.mcms.texture.tools;

import firemerald.mcms.Main;
import firemerald.mcms.gui.GuiElementContainer;
import firemerald.mcms.gui.GuiSection;
import firemerald.mcms.gui.components.ComponentFloatingLabel;
import firemerald.mcms.gui.components.text.ComponentIncrementFloat;
import firemerald.mcms.gui.components.text.ComponentTextFloat;
import firemerald.mcms.texture.Color;
import firemerald.mcms.texture.Texture;
import firemerald.mcms.util.MathUtil;
import firemerald.mcms.window.api.Modifier;

public class ToolBrush extends AbstractToolBrush
{
	public static final ToolBrush INSTANCE = new ToolBrush();
	
	public float size = 1;
	public float feather = 0;
	public boolean square = false;
	public boolean antiAlias = true;
	
	public static final Color OVERLAY = new Color(0, 0, 0, .5f);
	
	@Override
	public void drawOnOverlay(Texture tex, double u, double v)
	{
		draw(tex, u, v, OVERLAY);
	}
	
	@Override
	public void draw(Texture tex, double u, double v, Color color)
	{
		if (Modifier.CONTROL.isDown(Main.instance.window))
		{
			u = (Math.round(u * tex.w - 0.5f) + 0.5f) / tex.w;
			v = (Math.round(v * tex.h - 0.5f) + 0.5f) / tex.h;
		}
		drawLine(tex, u, v, u, v, color);
	}
	
	@Override
	public void drawLine(Texture tex, double u1, double v1, double u2, double v2, Color color)
	{
		float size = this.size * 0.5f; //because size is diameter
		float maxSize = size + feather; //because feather is not
		int w = tex.w;
		int h = tex.h;
		u1 *= w;
		v1 *= h;
		u2 *= w;
		v2 *= h;
		int minU = (int) Math.ceil(Math.min(u1, u2) - maxSize - 0.5f);
		int minV = (int) Math.ceil(Math.min(v1, v2) - maxSize - 0.5f);
		int maxU = (int) Math.floor(Math.max(u1, u2) + maxSize - 0.5f);
		int maxV = (int) Math.floor(Math.max(v1, v2) + maxSize - 0.5f);
		if (maxU < 0 || maxV < 0 || minU >= w || minV >= h) return;
		if (minU < 0) minU = 0;
		if (minV < 0) minV = 0;
		if (maxU >= w) maxU = w - 1;
		if (maxV >= h) maxV = h - 1;
		//double dv = minV + 0.5f - v2;
		for (int y = minV; y <= maxV; y++)
		{
			//double du = minU + 0.5f - u2;
			for (int x = minU; x <= maxU; x++)
			{
				float a;
				//float r = (float) (square ? Math.max(Math.abs(du), Math.abs(dv)) : Math.sqrt(du * du + dv * dv)); //TODO square brush
				//du++;
				float r = MathUtil.getDistanceFrom(x + .5f, y + .5f, (float) u1, (float) v1, (float) u2, (float) v2);
				if (r >= maxSize) continue;
				else if (r > size) a = (maxSize - r) / feather;
				else a = 1;
				if (!antiAlias)
				{
					if (a < 0.5f) continue;
					else a = 1;
				}
				Color src = new Color(color.c, color.a * a);
				Color des = tex.getPixel(x, y);
				tex.setPixel(x, y, Main.instance.toolHolder.getBlendMode().blend(src, des));
			}
			//dv++;
		}
	}

	private ComponentFloatingLabel labelSize;
	private ComponentTextFloat sizeT;
	private ComponentIncrementFloat sizeP, sizeS;
	private ComponentFloatingLabel labelFeather;
	private ComponentTextFloat featherT;
	private ComponentIncrementFloat featherP, featherS;

	@Override
	public void onSelect(GuiSection options)
	{
		super.onSelect(options);
		GuiElementContainer container = options.container;
		int minX = options.minX;
		int minY = options.minY + 40;
		int maxX = options.maxX;
		//float maxY = options.maxY;
		container.addElement(labelSize    = new ComponentFloatingLabel( minX      , minY     , maxX     , minY + 20 , Main.instance.fontMsg, "Size"));
		container.addElement(sizeT        = new ComponentTextFloat(     minX      , minY + 20, maxX - 10, minY + 40 , Main.instance.fontMsg, size, 0, Float.POSITIVE_INFINITY, value -> this.size = value));
		container.addElement(sizeP        = new ComponentIncrementFloat(maxX - 10 , minY + 20                       , sizeT, 1));
		container.addElement(sizeS        = new ComponentIncrementFloat(maxX - 10 , minY + 30                       , sizeT, -1));
		container.addElement(labelFeather = new ComponentFloatingLabel( minX      , minY + 40, maxX     , minY + 60 , Main.instance.fontMsg, "Feathering"));
		container.addElement(featherT     = new ComponentTextFloat(     minX      , minY + 60, maxX - 10, minY + 80 , Main.instance.fontMsg, feather, 0, Float.POSITIVE_INFINITY, value -> this.feather = value));
		container.addElement(featherP     = new ComponentIncrementFloat(maxX - 10 , minY + 60                       , featherT, 1));
		container.addElement(featherS     = new ComponentIncrementFloat(maxX - 10 , minY + 70                       , featherT, -1));
	}

	@Override
	public void onDeselect(GuiSection options)
	{
		super.onDeselect(options);
		GuiElementContainer container = options.container;
		container.removeElement(labelSize);
		container.removeElement(sizeT);
		container.removeElement(sizeP);
		container.removeElement(sizeS);
		container.removeElement(labelFeather);
		container.removeElement(featherT);
		container.removeElement(featherP);
		container.removeElement(featherS);
	}
}