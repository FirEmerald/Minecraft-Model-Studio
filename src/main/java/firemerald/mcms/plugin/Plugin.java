package firemerald.mcms.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import firemerald.mcms.events.EventBus;

/**
 * Annote plugin classes with this.<br>
 * The constructed plugin object will be run through {@link EventBus#registerListeners(Object handler)} to automatically register event listeners
 * 
 * @author FirEmerald
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Plugin
{
	/**
	 * The plugin's ID.
	 */
	String id();
	
	/**
	 * The plugin's human-readable display name.
	 */
	String name();
	
	/**
	 * The plugin's main author.
	 */
	String author();
	
	/**
	 * The plugin's version string. major.minor[.build[.revision]] is the preferred type, but any string can be used.
	 */
	String version() default "";
	
	/**
	 * The plugin's description - what it does, what mod or mods it may be for, ect.
	 */
	String description() default "";
	
	/**
	 * A string representation of a ResourceLocation for a 16x16 texture to be displayed next to the plugin entry in the plugins screen.
	 */
	String icon() default "";
	
	/**
	 * A list of people and/or projects to give credit to.
	 */
	String[] credits() default {};

	/**
	 * A list of dependencies' plugin IDs.
	 */
	String[] dependencies() default {};
}