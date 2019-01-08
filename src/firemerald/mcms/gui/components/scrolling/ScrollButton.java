package firemerald.mcms.gui.components.scrolling;

import firemerald.mcms.gui.components.ComponentButton;
import firemerald.mcms.model.Mesh;
import firemerald.mcms.theme.DirectionButtonFormat;

public abstract class ScrollButton extends ComponentButton
{
	public final IScrollable scrollable;
	public final Mesh outline = new Mesh();
	public DirectionButtonFormat rect;
	public final int direction;
	
	public ScrollButton(float x1, float y1, float x2, float y2, int direction, IScrollable scrollable)
	{
		super(x1, y1, x2, y2);
		this.scrollable = scrollable;
		this.direction = direction;
		setSize(x1, y1, x2, y2);
	}
	
	@Override
	public void setSize(float x1, float y1, float x2, float y2)
	{
		super.setSize(x1, y1, x2, y2);
		float w = x2 - x1, h = y2 - y1;
		outline.setMesh(0, 0, w, h, 0, 0, 0, 1, 1);
		rect = new DirectionButtonFormat((int) (x2 - x1), (int) (y2 - y1), 1, direction);
	}

	@Override
	public void render(ButtonState state)
	{
		state.applyButtonEffects();
		getTheme().bindScrollButton(rect);
		outline.render();
		state.removeButtonEffects();
	}
	
	@Override
	public boolean canScrollH(float mx, float my)
	{
		return this.isEnabled();
	}
	
	@Override
	public boolean canScrollV(float mx, float my)
	{
		return this.isEnabled();
	}

	@Override
	public void onMouseScroll(float mx, float my, float scrollX, float scrollY)
	{
		float size = scrollable.getMaxScroll();
		float scroll = scrollable.getScroll();
		scroll -= scrollY * 4;
		if (scroll < 0) scroll = 0;
		else if (scroll > size) scroll = size;
		scrollable.setScroll(scroll);
	}
	
	@Override
	public void onPress()
	{
		scroll();
	}

	@Override
	public void onRepeat()
	{
		scroll();
	}
	
	public abstract void scroll();
}