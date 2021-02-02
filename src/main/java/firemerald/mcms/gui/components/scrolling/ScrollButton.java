package firemerald.mcms.gui.components.scrolling;

import firemerald.mcms.gui.components.ComponentButton;
import firemerald.mcms.theme.EnumDirection;
import firemerald.mcms.theme.ThemeElement;
import firemerald.mcms.util.GuiUpdate;
import firemerald.mcms.util.mesh.GuiMesh;

public abstract class ScrollButton extends ComponentButton
{
	public final IScrollable scrollable;
	public final GuiMesh outline = new GuiMesh();
	public ThemeElement rect;
	public final EnumDirection direction;
	
	public ScrollButton(int x1, int y1, int x2, int y2, EnumDirection direction, IScrollable scrollable)
	{
		super(x1, y1, x2, y2);
		this.scrollable = scrollable;
		this.direction = direction;
		setSize(x1, y1, x2, y2);
	}
	
	@Override
	public void setSize(int x1, int y1, int x2, int y2)
	{
		super.setSize(x1, y1, x2, y2);
		outline.setMesh(x1, y1, x2, y2, 0, 0, 1, 1);
		onGuiUpdate(GuiUpdate.THEME);
	}
	
	@Override
	public void onGuiUpdate(GuiUpdate reason)
	{
		if (reason == GuiUpdate.THEME)
		{
			if (rect != null) rect.release();
			rect = getTheme().genScrollButton(x2 - x1, y2 - y1, 1, direction);
		}
	}

	@Override
	public void render(ButtonState state)
	{
		state.applyButtonEffects();
		rect.bind();
		outline.render();
		state.removeButtonEffects();
	}
	
	@Override
	public boolean canScrollV(float mx, float my)
	{
		return true;
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