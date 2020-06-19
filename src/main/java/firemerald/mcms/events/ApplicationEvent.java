package firemerald.mcms.events;

/**
 * These events are posted at specific points in the application's runtime.
 * 
 * @author FirEmerald
 *
 */
public class ApplicationEvent extends Event
{
	/**
	 * This is posted at the start of the program, just after plugins are constructed.
	 * 
	 * @author FirEmerald
	 *
	 */
	public static class PreInitialization extends ApplicationEvent {}
	/**
	 * This is posted after the program has finished initialization, just before the main logic loop begins running.
	 * 
	 * @author FirEmerald
	 *
	 */
	public static class Initialization extends ApplicationEvent {}
	/**
	 * This is posted immediately following the {@link Initialization} event, just before the main logic loop begins running.
	 * 
	 * @author FirEmerald
	 *
	 */
	public static class PostInitialization extends ApplicationEvent {}
	/**
	 * This is posted just before the program is terminated.
	 * 
	 * @author FirEmerald
	 *
	 */
	public static class Shutdown extends ApplicationEvent {}
}