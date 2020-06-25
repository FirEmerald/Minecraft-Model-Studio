package firemerald.mcms.util.history;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;

public class HistoryActionChangeDoubleValue implements IHistoryAction<HistoryActionChangeDoubleValue>
{
	public double val;
	public DoubleSupplier getter;
	public DoubleConsumer setter;
	
	public HistoryActionChangeDoubleValue(double val, DoubleSupplier getter, DoubleConsumer setter)
	{
		this.val = val;
		this.getter = getter;
		this.setter = setter;
	}
	
	@Override
	public HistoryActionChangeDoubleValue perform()
	{
		double val = getter.getAsDouble();
		setter.accept(this.val);
		this.val = val;
		return this;
	}
}