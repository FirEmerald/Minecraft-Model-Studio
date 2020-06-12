package firemerald.mcms.gui.main.components.tools;

import firemerald.mcms.Main;
import firemerald.mcms.texture.ColorModel;

public class ColorButtonPrimary extends ColorButton
{
	public ColorButtonPrimary(int x1, int y1, int x2, int y2)
	{
		super(x1, y1, x2, y2);
	}

	@Override
	public ColorModel getColor()
	{
		return Main.instance.toolHolder.getColor1().c;
	}

	@Override
	public void setColor(ColorModel color)
	{
		Main.instance.toolHolder.setColor1(color);
	}
}