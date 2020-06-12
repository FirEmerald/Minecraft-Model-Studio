package firemerald.mcms.gui.colors;

import firemerald.mcms.gui.components.ComponentPane;
import firemerald.mcms.texture.ColorModel;

public abstract class ComponentPaneColorPicker extends ComponentPane 
{
	public boolean hasChanged = false;
	
	public ComponentPaneColorPicker(int x1, int y1, int x2, int y2)
	{
		super(x1, y1, x2, y2);
	}
	
	public abstract ColorModel getColor();
	
	public abstract void setColor(ColorModel color);
}