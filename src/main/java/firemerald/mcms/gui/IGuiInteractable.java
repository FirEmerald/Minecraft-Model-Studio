package firemerald.mcms.gui;

import firemerald.mcms.util.history.IHistoryAction;
import firemerald.mcms.util.hotkey.Action;
import firemerald.mcms.window.api.Cursor;
import firemerald.mcms.window.api.Key;

public interface IGuiInteractable extends IGuiElement
{
	public abstract void onMousePressed(float mx, float my, int button, int mods);
	
	public abstract void onMouseRepeat(float mx, float my, int button, int mods);
	
	public abstract void onMouseReleased(float mx, float my, int button, int mods);

	public abstract void onDrag(float mx, float my, int button);
	
	default public boolean canScrollV(float mx, float my)
	{
		return false;
	}
	
	default public boolean canScrollH(float mx, float my)
	{
		return false;
	}
	
	public abstract void onMouseScroll(float mx, float my, float scrollX, float scrollY);
	
	public abstract void onCharTyped(char chr);
	
	public abstract boolean onKeyPressed(Key key, int scancode, int mods);
	
	public abstract boolean onKeyRepeat(Key key, int scancode, int mods);
	
	public abstract boolean onKeyReleased(Key key, int scancode, int mods);
	
	public abstract boolean onHotkey(Action action);
	
	public abstract boolean onAction(IHistoryAction<?> action);
	
	public abstract Cursor getCursor(float mx, float my);
}