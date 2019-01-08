package firemerald.mcms.gui.components.scrolling;

public class ScrollLeft extends ScrollButtonHorizontal
{
	public ScrollLeft(float x1, float y1, float x2, float y2, IScrollableHorizontal scrollable)
	{
		super(x1, y1, x2, y2, 3, scrollable);
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