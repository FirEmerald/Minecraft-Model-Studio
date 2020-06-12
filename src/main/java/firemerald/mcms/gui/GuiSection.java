package firemerald.mcms.gui;

public class GuiSection
{
	public final GuiElementContainer container;
	public int minX, minY, maxX, maxY;
	
	public GuiSection(GuiElementContainer container)
	{
		this(container, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
	}
	
	public GuiSection(GuiElementContainer container, int x, int y)
	{
		this(container, x, y, Integer.MAX_VALUE, Integer.MAX_VALUE);
	}
	
	public GuiSection(GuiElementContainer container, int minX, int minY, int maxX, int maxY)
	{
		this.container = container;
		setBounds(minX, minY, maxX, maxY);
	}
	
	public void setBounds(int minX, int minY, int maxX, int maxY)
	{
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
	}
	
	public void setMin(int x, int y)
	{
		setBounds(x, y, Integer.MAX_VALUE, Integer.MAX_VALUE);
	}
	
	public void setMax(int x, int y)
	{
		setBounds(Integer.MIN_VALUE, Integer.MIN_VALUE, x, y);
	}
	
	public void setAll()
	{
		setBounds(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
	}
}