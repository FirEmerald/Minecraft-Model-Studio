package firemerald.mcms.gui.components;

import firemerald.mcms.gui.IGuiInteractable;

public interface IComponent extends IGuiInteractable
{
	public abstract void onFocus();
	
	public abstract void onUnfocus();
	
	public abstract boolean contains(float x, float y);
	
	public abstract void setSize(int x1, int y1, int x2, int y2);
}