package firemerald.mcms.gui.components.scrolling;

public class ScrollRight extends ScrollButtonHorizontal
{
	public ScrollRight(float x1, float y1, float x2, float y2, IScrollableHorizontal scrollable)
	{
		super(x1, y1, x2, y2, 1, scrollable);
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