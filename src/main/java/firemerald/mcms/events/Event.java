package firemerald.mcms.events;

import org.apache.logging.log4j.Level;

import firemerald.mcms.Main;

public abstract class Event
{
	private boolean canceled = false;
	private final boolean canBeCanceled;
	
	public Event()
	{
		this.canBeCanceled = this.getClass().isAnnotationPresent(Cancelable.class);
	}
	
	public boolean isCanceled()
	{
		return canceled;
	}
	
	public void setCanceled(boolean canceled)
	{
		if (canBeCanceled) this.canceled = canceled;
		else Main.LOGGER.log(Level.WARN, "Tried to cancel a non-cancelable event: " + this.getClass().getName());
	}
}