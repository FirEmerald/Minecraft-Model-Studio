package firemerald.mcms.events;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *	annotate event handling events with this (methods with a single argument whos type is, or extends, {@link Event}) to allow them to be automatically added using the {@link EventBus}.registerListeners methods<br>
 **/
@Retention(value = RetentionPolicy.RUNTIME)
public @interface EventHandler 
{
	/**
	 * The event handler's priority. higher priority listeners run first.
	 */
	public int priority() default 0;
	
	/**
	 * Should this listener receive events that have been canceled?
	 */
	public boolean ignoreCanceled() default false;
}