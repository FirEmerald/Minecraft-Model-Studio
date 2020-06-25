package firemerald.mcms.util.history;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

public class HistoryActionChangeIntValue implements IHistoryAction<HistoryActionChangeIntValue>
{
	public int val;
	public IntSupplier getter;
	public IntConsumer setter;
	
	public HistoryActionChangeIntValue(int val, IntSupplier getter, IntConsumer setter)
	{
		this.val = val;
		this.getter = getter;
		this.setter = setter;
	}
	
	@Override
	public HistoryActionChangeIntValue perform()
	{
		int val = getter.getAsInt();
		setter.accept(this.val);
		this.val = val;
		return this;
	}
}