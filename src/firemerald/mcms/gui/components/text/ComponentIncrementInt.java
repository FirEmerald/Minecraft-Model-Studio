package firemerald.mcms.gui.components.text;

public class ComponentIncrementInt extends ComponentIncrement
{
	protected int increment;
	public final ComponentTextInt text;
	
	public ComponentIncrementInt(float x, float y, ComponentTextInt text, int increment)
	{
		super(x, y, increment < 0);
		this.text = text;
		this.increment = increment;
	}
	
	public void setIncrement(int increment)
	{
		this.increment = increment;
		id = increment < 0 ? DOWN : UP;
	}

	@Override
	public boolean isEnabled()
	{
		return increment < 0 ? text.getVal() > text.getMin() : text.getVal() < text.getMax();
	}

	@Override
	public void increment()
	{
		text.setVal(text.getVal() + increment);
	}
}