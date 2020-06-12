package firemerald.mcms.events;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *	annotate event handling events with this, if you are auto-registering them via the EventBus.addListeners methods
 * 	you can also just manually add event listener methods using the EventBus.addListener method
 **/
@Retention(value = RetentionPolicy.RUNTIME)
public @interface EventHandler 
{
	public int priority() default 0;
	
	public boolean ignoreCanceled() default false;
}