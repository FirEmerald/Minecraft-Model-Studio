package firemerald.mcms.gui.colors;

import firemerald.mcms.gui.components.ComponentPane;
import firemerald.mcms.texture.ColorModel;

public abstract class ComponentPaneColorPicker extends ComponentPane 
{
	public boolean hasChanged = false;
	
	public ComponentPaneColorPicker(float x1, float y1, float x2, float y2)
	{
		super(x1, y1, x2, y2);
	}
	
	public abstract ColorModel getColor();
	
	public abstract void setColor(ColorModel color);
}