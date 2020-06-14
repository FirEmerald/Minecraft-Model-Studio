package firemerald.mcms.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annote core mod classes with this - they <i>MUST</i> implement {@link ICoreMod} or they will not work!
 * 
 * @author FirEmerald
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface CoreMod
{
	/**
	 * The core mod/plugin's ID.
	 */
	String id();
	
	/**
	 * The core mod/plugin's human-readable display name.
	 */
	String name();
	
	/**
	 * The core mod/plugin's main author.
	 */
	String author();
	
	/**
	 * The core mod/plugin's version string. major.minor[.build[.revision]] is the preferred type, but any string can be used.
	 */
	String version() default "";
	
	/**
	 * The core mod/plugin's description - what it does, what mod or mods it may be for, ect.
	 */
	String description() default "";
	
	/**
	 * A string representation of a ResourceLocation for a 16x16 texture to be displayed next to the core mod/plugin entry in the plugins screen.
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