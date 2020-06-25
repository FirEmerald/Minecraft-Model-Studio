package firemerald.mcms.util.history;

import java.util.function.BooleanSupplier;
import firemerald.mcms.util.BooleanConsumer;

public class HistoryActionChangeBooleanValue implements IHistoryAction<HistoryActionChangeBooleanValue>
{
	public boolean val;
	public BooleanSupplier getter;
	public BooleanConsumer setter;
	
	public HistoryActionChangeBooleanValue(boolean val, BooleanSupplier getter, BooleanConsumer setter)
	{
		this.val = val;
		this.getter = getter;
		this.setter = setter;
	}
	
	@Override
	public HistoryActionChangeBooleanValue perform()
	{
		boolean val = getter.getAsBoolean();
		setter.accept(this.val);
		this.val = val;
		return this;
	}
}