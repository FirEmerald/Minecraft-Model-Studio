package firemerald.mcms.gui.components;

import firemerald.mcms.window.api.Cursor;

public class ComponentPanel extends ComponentPane
{
	protected boolean enabled = true;
	
	public ComponentPanel(int x1, int y1, int x2, int y2)
	{
		super(x1, y1, x2, y2);
	}
	
	public ComponentPanel(int x1, int y1, int x2, int y2, int margin)
	{
		super(x1, y1, x2, y2, margin);
	}
	
	public boolean isEnabled()
	{
		return enabled;
	}
	
	public void enable()
	{
		enabled = true;
	}
	
	public void disable()
	{
		enabled = false;
		if (this.focused != null) this.focused.onUnfocus();
		this.focused = null;
	}
	
	@Override
	public void onFocus()
	{
		if (enabled) super.onFocus();
	}
	
	@Override
	public boolean contains(float x, float y)
	{
		return enabled ? super.contains(x, y) : false;
	}

	@Override
	public void tick(float mx, float my, float deltaTime)
	{
		if (enabled) super.tick(mx, my, deltaTime);
	}

	@Override
	public void render(float mx, float my, boolean canHover)
	{
		if (enabled) super.render(mx, my, canHover);
	}

	@Override
	public void onMousePressed(float mx, float my, int button, int mods)
	{
		if (enabled) super.onMousePressed(mx, my, button, mods);
	}

	@Override
	public void onMouseRepeat(float mx, float my, int button, int mods) 
	{
		if (enabled) super.onMouseRepeat(mx, my, button, mods);
	}

	@Override
	public void onMouseReleased(float mx, float my, int button, int mods) 
	{
		if (enabled) super.onMouseReleased(mx, my, button, mods);
	}

	@Override
	public void onDrag(float mx, float my, int button)
	{
		if (enabled) super.onDrag(mx, my, button);
	}

	@Override
	public void onMouseScroll(float mx, float my, float scrollX, float scrollY) 
	{
		if (enabled) super.onMouseScroll(mx, my, scrollX, scrollY);
	}

	@Override
	public Cursor getCursor(float mx, float my)
	{
		return enabled ? super.getCursor(mx, my) : Cursor.STANDARD;
	}
}