package firemerald.mcms.gui.colors;

import firemerald.mcms.gui.components.Component;
import firemerald.mcms.texture.ColorModel;

public abstract class ColorPicker extends Component
{
	public boolean hasChanged = false;
	
	public ColorPicker(int x, int y)
	{
		super(x, y, x + 155, y + 155);
	}
	
	public abstract void setFromColor(ColorModel color);
	
	public abstract ColorModel getColor();
}