package firemerald.mcms.gui;

public interface IGuiInteractable extends IGuiElement
{
	public abstract void onMousePressed(float mx, float my, int button, int mods);
	
	public abstract void onMouseRepeat(float mx, float my, int button, int mods);
	
	public abstract void onMouseReleased(float mx, float my, int button, int mods);

	public abstract void onDrag(float mx, float my);
	
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
	
	public abstract void onKeyPressed(int key, int scancode, int mods);
	
	public abstract void onKeyRepeat(int key, int scancode, int mods);
	
	public abstract void onKeyReleased(int key, int scancode, int mods);
	
	public abstract long getCursor(float mx, float my);
}