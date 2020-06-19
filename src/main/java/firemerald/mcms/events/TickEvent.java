package firemerald.mcms.events;

/**
 * These events are posted during logic ticks
 * 
 * @author FirEmerald
 *
 */
public abstract class TickEvent extends Event
{
	/**
	 * How many nanoseconds have passed since the last logic tick
	 */
	public final long tickNanos;
	
	public TickEvent(long tickNanos)
	{
		this.tickNanos = tickNanos;
	}
	
	/**
	 * this is posted at the beginning of a logic tick
	 * 
	 * @author FirEmerald
	 *
	 */
	public static class Pre extends TickEvent
	{
		public Pre(long tickNanos)
		{
			super(tickNanos);
		}
	}
	
	/**
	 * this is posted at the end of a logic tick
	 * 
	 * @author FirEmerald
	 *
	 */
	public static class Post extends TickEvent
	{
		public Post(long tickNanos)
		{
			super(tickNanos);
		}
	}
}