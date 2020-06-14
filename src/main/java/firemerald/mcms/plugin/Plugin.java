package firemerald.mcms.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Plugin
{
	String id();
	
	String name();
	
	String author();
	
	String version() default "";
	
	String description() default "";
	
	String icon() default "";
	
	String[] credits() default {};
	
	String[] dependencies() default {};
}