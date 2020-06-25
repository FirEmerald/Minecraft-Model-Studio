package firemerald.mcms.events;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.Level;
import org.eclipse.jdt.annotation.NonNull;

import firemerald.mcms.Main;
import firemerald.mcms.plugin.PluginLoader;
import firemerald.mcms.plugin.PluginWrapper;
import firemerald.mcms.util.MiscUtil;

/** 
 * there are three ways to add event handling methods
 * the first and most efficient way is to directly invoke an addListener method with a method reference to the listener method - but this requires you to do this for every method
 * the second, most preferred way for large number of listener methods, is to use instance methods annotated with EventHandler, and call registerListeners with an instance of the handler object
 * the third method, which is discouraged, is to do like the second method but with static methods and the class reference instead.
 */
public class EventBus
{
	/**
	 * The event bus's name.
	 */
	public final String name;
	
	/**
	 * Constructs a new event bus.
	 * 
	 * @param name the event bus's name.
	 */
	public EventBus(String name)
	{
		this.name = name;
	}
	
	/**
	 * Represents an event handling method and it's properties
	 * 
	 * @author FirEmerald
	 *
	 * @param <E> the event class
	 */
	private static class ListenerInfo<E extends Event> implements Comparable<ListenerInfo<?>>
	{
		/**
		 * the event handler method
		 */
		private final Consumer<E> listener;
		/**
		 * the priority
		 */
		private final int priority;
		/**
		 * if the handler should handle canceled events
		 */
		private final boolean ignoreCanceled;
		/**
		 * the handler's owner plugin ID
		 */
		private final String plugin;
		
		/**
		 * Constructs a new ListenerInfo
		 * 
		 * @param listener the event handler method.
		 * @param priority the priority.
		 * @param ignoreCanceled if the handler should handle canceled events.
		 * @param plugin the handler's owner plugin ID.
		 */
		private ListenerInfo(Consumer<E> listener, int priority, boolean ignoreCanceled, String plugin)
		{
			this.listener = listener;
			this.priority = priority;
			this.ignoreCanceled = ignoreCanceled;
			this.plugin = plugin;
		}

		@Override
		public int compareTo(ListenerInfo<?> o)
		{
			return o.priority - this.priority;
		}
		
		/**
		 * Executes the handler on the event, respecting the canceled events handling flag
		 * 
		 * @param event the event
		 */
		private void call(E event)
		{
			if (!this.ignoreCanceled || !event.isCanceled())
			{
				PluginLoader.INSTANCE.activePlugin = plugin;
				listener.accept(event);
			}
		}
	}

	/**
	 * The event handlers
	 */
	private final Map<Class<? extends Event>, List<ListenerInfo<?>>> listeners = new HashMap<>();
	
	
	
	/**
	 * registers an event listener method for the specified class
	 * 
	 * @param eventClass the event to listen to
	 * @param listener an instance of {@link Listener} - usually a method reference or lambda expression - to execute when the event is fired.
	 */
	public <E extends Event> void addListener(Class<E> eventClass, Consumer<E> listener)
	{
		this.addListener(eventClass, listener, 0, false);
	}

	/**
	 * registers an event listener method for the specified class
	 * 
	 * @param eventClass the event to listen to
	 * @param listener an instance of {@link Listener} - usually a method reference or lambda expression - to execute when the event is fired.
	 * @param priority the handler's priority. higher priority handlers run first.
	 */
	public <E extends Event> void addListener(Class<E> eventClass, Consumer<E> listener, int priority)
	{
		this.addListener(eventClass, listener, priority, false);
	}

	/**
	 * registers an event listener method for the specified class
	 * 
	 * @param eventClass the event to listen to
	 * @param listener an instance of {@link Listener} - usually a method reference or lambda expression - to execute when the event is fired.
	 * @param ignoreCanceled if the listener should handle events that have already been canceled
	 */
	public <E extends Event> void addListener(Class<E> eventClass, Consumer<E> listener, boolean ignoreCanceled)
	{
		this.addListener(eventClass, listener, 0, ignoreCanceled);
	}

	/**
	 * registers an event listener method for the specified class
	 * 
	 * @param eventClass the event to listen to
	 * @param listener an instance of {@link Listener} - usually a method reference or lambda expression - to execute when the event is fired.
	 * @param priority the handler's priority. higher priority handlers run first.
	 * @param ignoreCanceled if the listener should handle events that have already been canceled
	 */
	public <E extends Event> void addListener(Class<E> eventClass, Consumer<E> listener, int priority, boolean ignoreCanceled)
	{
		addLambda(eventClass, listener, priority, ignoreCanceled, PluginLoader.INSTANCE.activePlugin);
	}
	
	/**
	 * The superclass of {@link Event}, used to terminate the loop that adds all sub-class event handlers for an event.
	 */
	private static final Class<?> EVENT_SUPER = Event.class.getSuperclass(); //Should be Object.class but I'll leave this here as a just-in-case measure

	/**
	 * registers an event listener method for the specified class from the specified plugin ID
	 * 
	 * @param eventClass the event to listen to
	 * @param listener an instance of {@link Listener} - usually a method reference or lambda expression - to execute when the event is fired.
	 * @param priority the handler's priority. higher priority handlers run first.
	 * @param ignoreCanceled if the listener should handle events that have already been canceled
	 * @param plugin the plugin ID
	 */
	private <E extends Event> void addLambda(Class<? super E> eventClass, Consumer<E> listener, int priority, boolean ignoreCanceled, String plugin)
	{
		addLambda(eventClass, new ListenerInfo<>(listener, priority, ignoreCanceled, plugin));
	}

	/**
	 * registers an event listener method for the specified class from the specified plugin ID
	 * 
	 * @param eventClass the event to listen to
	 * @param info the event listener info
	 */
	@SuppressWarnings("unchecked")
	private <E extends Event> void addLambda(Class<? super E> eventClass, ListenerInfo<E> info)
	{
		List<ListenerInfo<?>> listeners;
		if ((listeners = this.listeners.get(eventClass)) == null) //build a list from the existing subclass lists
		{
			List<Stream<ListenerInfo<?>>> streams = new ArrayList<>();
			Class<?> clazz = eventClass;
			while (clazz != EVENT_SUPER)
			{
				List<ListenerInfo<?>> listeners2;
				if ((listeners2 = this.listeners.get(clazz)) != null) streams.add(listeners2.stream());
				clazz = clazz.getSuperclass();
			}
			this.listeners.put((Class<? extends Event>) eventClass, listeners = new ArrayList<>(streams.stream().flatMap(stream -> stream).collect(Collectors.toList())));
		}
		listeners.add(info);
		this.listeners.forEach((clazz, list) -> { //register to all superclass lists
			if (eventClass != clazz && eventClass.isAssignableFrom(clazz))
			{
				list.add(info);
				Collections.sort(list);
			}
		});
	}

    /**
     * A method handles lookup instance
     */
    private static final Lookup LOOKUP = MethodHandles.lookup();
	/**
	 * The functional interface type of {@link Listener}
	 */
	private static final MethodType LISTENER_TYPE = MethodType.methodType(Consumer.class);
    /**
     * The method type of event methods
     */
    private static final MethodType EVENT_METHOD = MethodType.methodType(Void.TYPE, Object.class);
	
	/**
	 * Registers static event handler methods automatically from a class<br>
	 * 
	 * @param c the class to register static event handler methods from
	 * 
	 * @see {@link EventHandler}
	 */
	public <E extends Event> void registerListeners(Class<?> c)
	{
		for (Method m : c.getDeclaredMethods())
		{
			if ((m.getModifiers() & Modifier.STATIC) != 0 && m.isAnnotationPresent(EventHandler.class))
			{
				Class<?>[] params;
				Class<?> clazz;
				if ((params = m.getParameterTypes()).length == 1 && Event.class.isAssignableFrom(clazz = params[0]))
				{
					@SuppressWarnings("unchecked")
					Class<E> eventClass = (Class<E>) clazz;
					try
					{
						MethodHandle mh = LOOKUP.unreflect(m);
				        MethodType type = MethodType.methodType(Void.TYPE, eventClass);
						EventHandler annote = m.getAnnotation(EventHandler.class);
						addLambda(eventClass, (Consumer<E>) LambdaMetafactory.metafactory(LOOKUP, "accept", LISTENER_TYPE, EVENT_METHOD, mh, type).getTarget().invoke(), annote.priority(), annote.ignoreCanceled(), PluginLoader.INSTANCE.activePlugin);
					}
					catch (Throwable e)
					{
						Main.LOGGER.log(Level.WARN, "Unable to add listener for class " + c.getName() + " method " + m.toString(), e);
					}
				}
				else Main.LOGGER.log(Level.WARN, "Invalid type arguments for EventHandler method " + m + ", must be a single parameter that extends Event.");
			}
		}
	}

	/**
	 * Registers instance event handler methods automatically from an object<br>
	 * 
	 * @param handler the object to register instance event handler methods from
	 * 
	 * @see {@link EventHandler}
	 */
	public <E extends Event> void registerListeners(@NonNull Object handler)
	{
		Class<?> c = handler.getClass();
        MethodType inT = LISTENER_TYPE.appendParameterTypes(c);
		for (Method m : c.getMethods())
		{
			if ((m.getModifiers() & Modifier.STATIC) == 0 && m.isAnnotationPresent(EventHandler.class))
			{
				Class<?>[] params;
				Class<?> clazz;
				if ((params = m.getParameterTypes()).length == 1 && Event.class.isAssignableFrom(clazz = params[0]))
				{
					@SuppressWarnings("unchecked")
					Class<E> eventClass = (Class<E>) clazz;
					try
					{
						MethodHandle mh = LOOKUP.unreflect(m);
				        MethodType type = MethodType.methodType(Void.TYPE, eventClass);
						EventHandler annote = m.getAnnotation(EventHandler.class);
						addLambda(eventClass, (Consumer<E>) LambdaMetafactory.metafactory(LOOKUP, "accept", inT, EVENT_METHOD, mh, type).getTarget().invoke(handler), annote.priority(), annote.ignoreCanceled(), PluginLoader.INSTANCE.activePlugin);
					}
					catch (Throwable e)
					{
						Main.LOGGER.log(Level.WARN, "Unable to add listener for class " + c.getName() + " method " + m.toString(), e);
					}
				}
				else Main.LOGGER.log(Level.WARN, "Invalid type arguments for EventHandler method " + m + ", must be a single parameter that extends Event.");
			}
		}
	}

	/**
	 * Registers instance event handler methods automatically from a plugin<br>
	 * 
	 * @param wrapper the plugin's wrapper
	 * 
	 * @see {@link EventHandler}
	 */
	public <E extends Event> void registerPluginListeners(PluginWrapper wrapper)
	{
		registerListeners(wrapper.getPlugin());
	}
	
	/**
	 * Posts an event to the event bus, running all the listeners for that event and it's subclasses.
	 * 
	 * @param event the event post
	 * 
	 * @return if the event was canceled
	 */
	@SuppressWarnings("unchecked")
	public <E extends Event> boolean post(E event)
	{
		if (Main.instance.state.eventLogging) //log event times
		{
			List<ListenerInfo<?>> list = this.listeners.get(event.getClass());
			if (list != null)
			{
				String prevID = PluginLoader.INSTANCE.activePlugin;
				Main.LOGGER.log(Level.DEBUG, "Firing " + event.getClass().getName() + " on " + name);
				long time = System.nanoTime();
				list.forEach(info -> {
					long time2 = System.nanoTime();
					((ListenerInfo<? super E>) info).call(event);
					long endTime = System.nanoTime();
					Main.LOGGER.log(Level.DEBUG, "Firing on " + info.plugin + " Took " + MiscUtil.toSecondsDecimal(endTime - time2) + " seconds");
				});
				PluginLoader.INSTANCE.activePlugin = prevID;
				long endTime = System.nanoTime();
				Main.LOGGER.log(Level.DEBUG, "Took " + MiscUtil.toSecondsDecimal(endTime - time) + " seconds");
			}
			else Main.LOGGER.log(Level.DEBUG, "Not firing " + event.getClass().getName() + " on " + name + " as there are no registered listeners for the event");
		}
		else
		{
			List<ListenerInfo<?>> list = this.listeners.get(event.getClass());
			if (list != null)
			{
				String prevID = PluginLoader.INSTANCE.activePlugin;
				list.forEach(info -> 
				((ListenerInfo<? super E>) info).call(event));
				PluginLoader.INSTANCE.activePlugin = prevID;
			}
		}
		return event.isCanceled();
	}
}