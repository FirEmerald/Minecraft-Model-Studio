package firemerald.mcms.gui.components.scrolling;

import firemerald.mcms.theme.EnumDirection;

public class ScrollUp extends ScrollButton
{
	public ScrollUp(int x1, int y1, int x2, int y2, IScrollable scrollable)
	{
		super(x1, y1, x2, y2, EnumDirection.UP, scrollable);
	}
	
	@Override
	public boolean isEnabled()
	{
		return scrollable.getScroll() > 0;
	}
	
	@Override
	public void scroll()
	{
		float scroll = scrollable.getScroll();
		scroll -= 4;
		if (scroll < 0) scroll = 0;
		scrollable.setScroll(scroll);
	}
}