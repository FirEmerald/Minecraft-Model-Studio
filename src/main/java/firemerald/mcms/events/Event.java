package firemerald.mcms.events;

import org.apache.logging.log4j.Level;

import firemerald.mcms.Main;

/**
 * The base event class.
 * 
 * @author FirEmerald
 *
 */
public abstract class Event
{
	/**
	 * If the event has been canceled
	 */
	private boolean canceled = false;
	/**
	 * If the event can be canceled
	 */
	private final boolean canBeCanceled;
	
	/**
	 * Constructs the event
	 */
	public Event()
	{
		this.canBeCanceled = this.getClass().isAnnotationPresent(Cancelable.class);
	}
	
	/**
	 * @return if the event has been canceled
	 */
	public boolean isCanceled()
	{
		return canceled;
	}
	
	/**
	 * Sets the event's canceled flag, if possible.<br>
	 * If the event is not {@link Cancelable} a warning will be logged.<br>
	 * This can be used to un-cancel events as well, although great care should be taken when doing so. 
	 * 
	 * @param canceled if the event is canceled.
	 */
	public void setCanceled(boolean canceled)
	{
		if (canBeCanceled) this.canceled = canceled;
		else Main.LOGGER.log(Level.WARN, "Tried to cancel a non-cancelable event: " + this.getClass().getName());
	}
}