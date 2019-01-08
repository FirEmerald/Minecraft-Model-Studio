package firemerald.mcms.gui.components;

import firemerald.mcms.util.Cursors;

public abstract class Component implements IComponent
{
	public float x1, y1, x2, y2;
	public boolean focused = false;
	
	public Component(float x1, float y1, float x2, float y2)
	{
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
	
	@Override
	public void setSize(float x1, float y1, float x2, float y2)
	{
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
	
	@Override
	public void onFocus()
	{
		this.focused = true;
	}
	
	@Override
	public void onUnfocus()
	{
		this.focused = false;
	}

	@Override
	public void onMousePressed(float mx, float my, int button, int mods) {}

	@Override
	public void onMouseRepeat(float mx, float my, int button, int mods) {}

	@Override
	public void onMouseReleased(float mx, float my, int button, int mods) {}

	@Override
	public void onDrag(float mx, float my) {}

	@Override
	public void onMouseScroll(float mx, float my, float scrollX, float scrollY) {}

	@Override
	public void tick(float mx, float my, float deltaTime) {}

	@Override
	public void onCharTyped(char chr) {}

	@Override
	public void onKeyPressed(int key, int scancode, int mods) {}

	@Override
	public void onKeyReleased(int key, int scancode, int mods) {}

	@Override
	public void onKeyRepeat(int key, int scancode, int mods) {}

	@Override
	public long getCursor(float mx, float my)
	{
		return Cursors.standard;
	}
	
	@Override
	public boolean contains(float x, float y)
	{
		return (x >= x1 && y >= y1 && x < x2 && y < y2);
	}
	
	@Override
	public float getX1()
	{
		return x1;
	}
	
	@Override
	public float getY1()
	{
		return y1;
	}
	
	@Override
	public float getX2()
	{
		return x2;
	}
	
	@Override
	public float getY2()
	{
		return y2;
	}
}