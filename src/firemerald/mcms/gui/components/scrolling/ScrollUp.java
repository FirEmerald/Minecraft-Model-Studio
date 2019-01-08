package firemerald.mcms.gui.components.scrolling;

public class ScrollUp extends ScrollButton
{
	public ScrollUp(float x1, float y1, float x2, float y2, IScrollable scrollable)
	{
		super(x1, y1, x2, y2, 0, scrollable);
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