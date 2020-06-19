package firemerald.mcms.events;

/**
 * These events are posted during rendering
 * 
 * @author FirEmerald
 *
 */
public abstract class RenderEvent extends Event
{	
	/**
	 * this is posted before rendering
	 * 
	 * @author FirEmerald
	 *
	 */
	public static class Pre extends RenderEvent {}
	
	/**
	 * this is posted after rendering
	 * 
	 * @author FirEmerald
	 *
	 */
	public static class Post extends RenderEvent {}
}