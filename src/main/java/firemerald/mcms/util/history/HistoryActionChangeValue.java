package firemerald.mcms.util.history;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class HistoryActionChangeValue<T> implements IHistoryAction<HistoryActionChangeValue<T>>
{
	private T val;
	public final Supplier<T> getter;
	public final Consumer<T> setter;
	
	public HistoryActionChangeValue(T val, Supplier<T> getter, Consumer<T> setter)
	{
		this.val = val;
		this.getter = getter;
		this.setter = setter;
	}
	
	@Override
	public HistoryActionChangeValue<T> perform()
	{
		T val = getter.get();
		setter.accept(this.val);
		this.val = val;
		return this;
	}
}