package firemerald.mcms.api.util;

public interface ISelfTyped<T extends ISelfTyped<T>>
{
	@SuppressWarnings("unchecked")
	public default T self()
	{
		return (T) this;
	}
}