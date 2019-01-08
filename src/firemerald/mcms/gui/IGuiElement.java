package firemerald.mcms.gui;

public interface IGuiElement
{
	public abstract void tick(float mx, float my, float deltaTime);
	
	public abstract void render(float mx, float my, boolean canHover);
	
	public abstract float getX1();
	
	public abstract float getY1();
	
	public abstract float getX2();
	
	public abstract float getY2();
}