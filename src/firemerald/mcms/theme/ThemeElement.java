package firemerald.mcms.theme;

public abstract class ThemeElement
{
	public final GuiTheme theme;
	boolean released = false;
	
	ThemeElement(GuiTheme theme)
	{
		this.theme = theme;
	}
	
	public void release()
	{
		if (!released)
		{
			released = true;
			releaseElement();
		}
	}
	
	@Override
	public void finalize()
	{
		release();
	}
	
	public abstract void bind();
	
	protected abstract void releaseElement();
}