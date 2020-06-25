package firemerald.mcms.gui.components.text;

import firemerald.mcms.Main;
import firemerald.mcms.util.history.HistoryAction;

public class ComponentIncrementFloat extends ComponentIncrement
{
	protected float increment;
	public final ComponentTextFloat text;
	
	public ComponentIncrementFloat(int x, int y, ComponentTextFloat text, float increment)
	{
		super(x, y, increment < 0);
		this.text = text;
		this.increment = increment;
	}
	
	public void setIncrement(int increment)
	{
		this.increment = increment;
		setIsNegative(increment < 0);
	}

	@Override
	public boolean isEnabled()
	{
		return increment < 0 ? text.getVal() > text.getMin() : text.getVal() < text.getMax();
	}

	@Override
	public void increment()
	{
		final float prevVal = text.getVal();
		final float newVal = prevVal + increment;
		Main.instance.project.onAction(new HistoryAction(() -> text.setVal(prevVal), () -> text.setVal(newVal)));
		text.setVal(text.getVal() + increment);
	}
}