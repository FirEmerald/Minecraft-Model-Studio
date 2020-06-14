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
	public final String name;
	
	public EventBus(String name)
	{
		this.name = name;
	}
	
	@FunctionalInterface
	public static interface Listener<E extends Event>
	{
		public void call(E event);
	}
	
	private static class ListenerInfo<E extends Event> implements Comparable<ListenerInfo<?>>
	{
		private final Listener<E> listener;
		private final int priority;
		private final boolean ignoreCanceled;
		private final String plugin;
		
		private ListenerInfo(Listener<E> listener, int priority, boolean ignoreCanceled, String plugin)
		{
			this.listener = listener;
			this.priority = priority;
			this.ignoreCanceled = ignoreCanceled;
			this.plugin = plugin;
		}

		@Override
		public int compareTo(ListenerInfo<?> o) //reverse because higher priority goes last
		{
			return o.priority - this.priority;
		}
		
		private void call(E event)
		{
			if (!this.ignoreCanceled || !event.isCanceled())
			{
				PluginLoader.INSTANCE.activePlugin = plugin;
				listener.call(event);
			}
		}
	}

	private final Map<Class<? extends Event>, ArrayList<ListenerInfo<?>>> listeners = new HashMap<>();

	/** 
	 * use like this:
	 * addListener(Event Class, [Class]::[Method]); //for static methods
	 * or
	 * addListener(Event Class, [instance]::[Method]); //for instance methods
	 */
	public <E extends Event> void addListener(Class<E> eventClass, Listener<E> listener)
	{
		this.addListener(eventClass, listener, 0, false);
	}

	/** 
	 * use like this:
	 * addListener(Event Class, [Class]::[Method], priority); //for static methods
	 * or
	 * addListener(Event Class, [instance]::[Method], priority); //for instance methods
	 */
	public <E extends Event> void addListener(Class<E> eventClass, Listener<E> listener, int priority)
	{
		this.addListener(eventClass, listener, priority, false);
	}

	/** 
	 * use like this:
	 * addListener(Event Class, [Class]::[Method], ignoreCanceled); //for static methods
	 * or
	 * addListener(Event Class, [instance]::[Method], ignoreCanceled); //for instance methods
	 */
	public <E extends Event> void addListener(Class<E> eventClass, Listener<E> listener, boolean ignoreCanceled)
	{
		this.addListener(eventClass, listener, 0, ignoreCanceled);
	}

	/** 
	 * use like this:
	 * addListener(Event Class, [Class]::[Method], priority, ignoreCanceled); //for static methods
	 * or
	 * addListener(Event Class, [instance]::[Method], priority, ignoreCanceled); //for instance methods
	 */
	public <E extends Event> void addListener(Class<E> eventClass, Listener<E> listener, int priority, boolean ignoreCanceled)
	{
		addLambda(eventClass, listener, priority, ignoreCanceled, PluginLoader.INSTANCE.activePlugin);
	}
	
	private <E extends Event> void addLambda(Class<E> eventClass, Listener<E> listener, int priority, boolean ignoreCanceled, String plugin)
	{
		ArrayList<ListenerInfo<?>> listeners;
		if ((listeners = this.listeners.get(eventClass)) == null) this.listeners.put(eventClass, listeners = new ArrayList<>());
		listeners.add(new ListenerInfo<E>(listener, priority, ignoreCanceled, plugin));
		Collections.sort(listeners);
	}

    private static final Lookup LOOKUP = MethodHandles.lookup();
	private static final MethodType LISTENER_TYPE = MethodType.methodType(Listener.class);
    private static final MethodType EVENT_METHOD = MethodType.methodType(Void.TYPE, Event.class);
	
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
						addLambda(eventClass, (Listener<E>) LambdaMetafactory.metafactory(LOOKUP, "call", LISTENER_TYPE, EVENT_METHOD, mh, type).getTarget().invoke(), annote.priority(), annote.ignoreCanceled(), PluginLoader.INSTANCE.activePlugin);
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
						addLambda(eventClass, (Listener<E>) LambdaMetafactory.metafactory(LOOKUP, "call", inT, EVENT_METHOD, mh, type).getTarget().invoke(handler), annote.priority(), annote.ignoreCanceled(), PluginLoader.INSTANCE.activePlugin);
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
	
	public <E extends Event> void registerPluginListeners(PluginWrapper wrapper)
	{
		Object plugin = wrapper.getPlugin();
		Class<?> c = plugin.getClass();
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
						addLambda(eventClass, (Listener<E>) LambdaMetafactory.metafactory(LOOKUP, "call", inT, EVENT_METHOD, mh, type).getTarget().invoke(plugin), annote.priority(), annote.ignoreCanceled(), wrapper.id);
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
	
	private static final Class<?> EVENT_SUPER = Event.class.getSuperclass(); //Should be Object.class but I'll leave this here as a just-in-case measure
	
	@SuppressWarnings("unchecked")
	public <E extends Event> boolean post(E event)
	{
		Main.LOGGER.log(Level.DEBUG, "Firing " + event.getClass().getName() + " on " + name);
		long time = System.nanoTime();
		List<Stream<ListenerInfo<?>>> streams = new ArrayList<>();
		Class<?> clazz = event.getClass();
		while (clazz != EVENT_SUPER)
		{
			ArrayList<ListenerInfo<?>> listeners;
			if ((listeners = this.listeners.get(clazz)) != null) streams.add(listeners.stream());
			clazz = clazz.getSuperclass();
		}
		streams.stream().flatMap(stream -> stream).sorted().forEach(listener -> ((ListenerInfo<? super E>) listener).call(event));
		PluginLoader.INSTANCE.activePlugin = Main.ID;
		long endTime = System.nanoTime();
		Main.LOGGER.log(Level.DEBUG, "Took " + MiscUtil.toSecondsDecimal(endTime - time) + " seconds");
		return event.isCanceled();
	}
}