package firemerald.mcms.gui.components.text;

public class ComponentIncrementFloat extends ComponentIncrement
{
	protected float increment;
	public final ComponentTextFloat text;
	
	public ComponentIncrementFloat(float x, float y, ComponentTextFloat text, float increment)
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