package firemerald.mcms.gui.components.scrolling;

public class ScrollDown extends ScrollButton
{
	public ScrollDown(float x1, float y1, float x2, float y2, IScrollable scrollable)
	{
		super(x1, y1, x2, y2, 2, scrollable);
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