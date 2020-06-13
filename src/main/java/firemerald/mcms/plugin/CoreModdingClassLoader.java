package firemerald.mcms.plugin;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.logging.log4j.Logger;

/**
 * Custom {@link ClassLoader} that coremods classes.
 * 
 * @author FirEmerald
 */
public class CoreModdingClassLoader extends URLClassLoader
{
	public final Logger logger;
	/**
	 * The Method instance for {@link PluginLoader#modStatic(String, byte[])}
	 */
	public Method mod_method;
	
    /**
     * Constructs the classloader to use the URL's from the original classloader.
     * 
     * @param sources the URL sources from the original classloader
     */
    public CoreModdingClassLoader(URL[] sources, Logger logger)
    {
        super(sources, null);
        this.logger = logger;
        try
        {
			mod_method = Class.forName("firemerald.mcms.plugin.PluginLoader", true, this).getMethod("modStatic", String.class, byte[].class);
		}
        catch (NoSuchMethodException | SecurityException | ClassNotFoundException e)
        {
        	logger.warn("Failed to initialize coremodding features", e);
		}
    }

	/**
	 * Finds and loads the class with the specified name from the URL searchpath. Any URLs referring to JAR files are loaded and opened as neededuntil the class is found.
	 * 
	 * @param name the name of the class
	 * @return the resulting class
	 * @throws ClassNotFoundException if the class could not be found, or if the loader is closed, or if coremodding failed and {@link #exitOnCoremodFail} is true.
	 * @throws NullPointerException if the name is null.
	 */
	@Override
	public Class<?> findClass(String name) throws ClassNotFoundException
	{
		try
		{
			InputStream classData = getResourceAsStream(name.replace('.', '/') + ".class");
			if (classData == null)
			{
				throw new ClassNotFoundException(name);
			}
			byte[] array;
			byte[] prev = array = new byte[0];
			byte[] data = new byte[1024];
			int num;
			while ((num = classData.read(data)) > 0)
			{
				array = new byte[prev.length + num];
				System.arraycopy(prev, 0, array, 0, prev.length);
				System.arraycopy(data, 0, array, prev.length, num);
				prev = array;
			}
			classData.close();
			if (mod_method != null) try 
			{
				array = (byte[]) mod_method.invoke(null, name, array);
			}
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
			{
	        	if (exceptionOnCoremodFail) throw new ClassNotFoundException("Failed to coremod " + name, e);
	        	else logger.warn("Failed to coremod " + name, e);
			}
			return defineClass(name, array, 0, array.length);
		}
		catch (IOException e)
		{
			throw new ClassNotFoundException("", e);
		}
    }
	
	/**
	 * Whether the classloader should generate a {@link ClassNotFoundException} when coremodding fails, or just print a warning.
	 */
	public boolean exceptionOnCoremodFail = true;
}