package firemerald.mcms.gui.components.scrolling;

import firemerald.mcms.theme.EnumDirection;

public class ScrollRight extends ScrollButtonHorizontal
{
	public ScrollRight(int x1, int y1, int x2, int y2, IScrollableHorizontal scrollable)
	{
		super(x1, y1, x2, y2, EnumDirection.RIGHT, scrollable);
	}
	
	@Override
	public boolean isEnabled()
	{
		return scrollable.getScrollH() < scrollable.getMaxScrollH();
	}
	
	@Override
	public void scroll()
	{
		float size = scrollable.getMaxScrollH();
		float scroll = scrollable.getScrollH();
		scroll += 4;
		if (scroll > size) scroll = size;
		scrollable.setScrollH(scroll);
	}
}