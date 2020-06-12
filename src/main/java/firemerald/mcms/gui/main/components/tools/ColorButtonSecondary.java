package firemerald.mcms.gui.main.components.tools;

import firemerald.mcms.Main;
import firemerald.mcms.texture.ColorModel;

public class ColorButtonSecondary extends ColorButton
{
	public ColorButtonSecondary(int x1, int y1, int x2, int y2)
	{
		super(x1, y1, x2, y2);
	}

	@Override
	public ColorModel getColor()
	{
		return Main.instance.toolHolder.getColor2().c;
	}

	@Override
	public void setColor(ColorModel color)
	{
		Main.instance.toolHolder.setColor2(color);
	}
}