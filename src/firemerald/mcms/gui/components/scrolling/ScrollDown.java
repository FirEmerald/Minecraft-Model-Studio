package firemerald.mcms.gui.components.scrolling;

import firemerald.mcms.theme.EnumDirection;

public class ScrollDown extends ScrollButton
{
	public ScrollDown(int x1, int y1, int x2, int y2, IScrollable scrollable)
	{
		super(x1, y1, x2, y2, EnumDirection.DOWN, scrollable);
	}
	
	@Override
	public boolean isEnabled()
	{
		return scrollable.getScroll() < scrollable.getMaxScroll();
	}
	
	@Override
	public void scroll()
	{
		float size = scrollable.getMaxScroll();
		float scroll = scrollable.getScroll();
		scroll += 4;
		if (scroll > size) scroll = size;
		scrollable.setScroll(scroll);
	}
}