package firemerald.mcms.util.history;

import firemerald.mcms.util.FloatConsumer;
import firemerald.mcms.util.FloatSupplier;

public class HistoryActionChangeFloatValue implements IHistoryAction<HistoryActionChangeFloatValue>
{
	public float val;
	public FloatSupplier getter;
	public FloatConsumer setter;
	
	public HistoryActionChangeFloatValue(float val, FloatSupplier getter, FloatConsumer setter)
	{
		this.val = val;
		this.getter = getter;
		this.setter = setter;
	}
	
	@Override
	public HistoryActionChangeFloatValue perform()
	{
		float val = getter.getAsFloat();
		setter.accept(this.val);
		this.val = val;
		return this;
	}
}