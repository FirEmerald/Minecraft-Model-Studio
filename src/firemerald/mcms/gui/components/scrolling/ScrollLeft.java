package firemerald.mcms.gui.components.scrolling;

import firemerald.mcms.theme.EnumDirection;

public class ScrollLeft extends ScrollButtonHorizontal
{
	public ScrollLeft(int x1, int y1, int x2, int y2, IScrollableHorizontal scrollable)
	{
		super(x1, y1, x2, y2, EnumDirection.LEFT, scrollable);
	}
	
	@Override
	public boolean isEnabled()
	{
		return scrollable.getScrollH() > 0;
	}
	
	@Override
	public void scroll()
	{
		float scroll = scrollable.getScrollH();
		scroll -= 4;
		if (scroll < 0) scroll = 0;
		scrollable.setScrollH(scroll);
	}
}