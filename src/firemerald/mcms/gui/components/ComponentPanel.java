package firemerald.mcms.gui.components;

import firemerald.mcms.util.Cursors;

public class ComponentPanel extends ComponentPane
{
	private boolean enabled = true;
	
	public ComponentPanel(float x1, float y1, float x2, float y2)
	{
		super(x1, y1, x2, y2);
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
		this.focused.onUnfocus();
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
	public void onDrag(float mx, float my)
	{
		if (enabled) super.onDrag(mx, my);
	}

	@Override
	public void onMouseScroll(float mx, float my, float scrollX, float scrollY) 
	{
		if (enabled) super.onMouseScroll(mx, my, scrollX, scrollY);
	}

	@Override
	public long getCursor(float mx, float my)
	{
		return enabled ? super.getCursor(mx, my) : Cursors.standard;
	}
}