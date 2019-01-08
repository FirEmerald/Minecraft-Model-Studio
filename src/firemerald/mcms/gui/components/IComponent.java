package firemerald.mcms.gui.components;

import firemerald.mcms.Main;
import firemerald.mcms.gui.IGuiInteractable;
import firemerald.mcms.theme.GuiTheme;

public interface IComponent extends IGuiInteractable
{
	public abstract void onFocus();
	
	public abstract void onUnfocus();
	
	public abstract boolean contains(float x, float y);
	
	public abstract void setSize(float x1, float y1, float x2, float y2);
	
	default public GuiTheme getTheme()
	{
		return Main.instance.theme;
	}
}