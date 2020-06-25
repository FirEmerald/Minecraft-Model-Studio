package firemerald.mcms.gui.components.text;

import firemerald.mcms.Main;
import firemerald.mcms.util.history.HistoryAction;

public class ComponentIncrementDouble extends ComponentIncrement
{
	protected double increment;
	public final ComponentTextDouble text;
	
	public ComponentIncrementDouble(int x, int y, ComponentTextDouble text, double increment)
	{
		super(x, y, increment < 0);
		this.text = text;
		this.increment = increment;
	}
	
	public void setIncrement(double increment)
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
		final double prevVal = text.getVal();
		final double newVal = prevVal + increment;
		Main.instance.project.onAction(new HistoryAction(() -> text.setVal(prevVal), () -> text.setVal(prevVal)));
		text.setVal(newVal);
	}
}