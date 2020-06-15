package firemerald.mcms.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import firemerald.mcms.Main;
import firemerald.mcms.events.EventBus;

/**
 * The main plugin loading handler. 
 * 
 * @author FirEmerald
 *
 */
@CoreModExcluded
public class PluginLoader
{
	/**
	 * The {@link URLClassLoader#addURL(URL url)} method
	 */
	public static final Method ADD_URL;
	/**
	 * The plugin loader instance
	 */
	public static final PluginLoader INSTANCE = new PluginLoader();
	/**
	 * A list of all discovered plugin candidates
	 */
	public final List<PluginCandidate> pluginCandidates = new ArrayList<>();
	/**
	 * the plugin loader logger instance
	 */
	public static final Logger LOGGER = LogManager.getLogger("MSMS Plugin Loader");
	
    static
    {
		try
		{
			ADD_URL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
		}
		catch (NoSuchMethodException | SecurityException e)
		{
			throw new IllegalStateException("Couldn't access addURL.addURL(URL) method reflectively", e);
		}
		ADD_URL.setAccessible(true);
	}

	/**
	 * All the active loaded core mods
	 */
	public final Map<String, CoreModWrapper> loadedCoreMods = new LinkedHashMap<>();
	/**
	 * All the active loaded plugins
	 */
	public final Map<String, AbstractPluginWrapper> loadedPlugins = new LinkedHashMap<>();
	/**
	 * All the reigstered class-specific core modders
	 */
	public final Map<String, List<ICoreModder>> coreModders = new HashMap<>();
	/**
	 * All the registered global core modders
	 */
	public final List<ICoreModder> globalCoreModders = new ArrayList<>();
	/**
	 * A temporary list of core mod wrappers
	 */
	private List<CoreModWrapper> coreMods;
	/**
	 * A temporary list of plugin wrappers
	 */
	private List<PluginWrapper> plugins;
	
	/**
	 * passes the call on to the instance for coremodding
	 * 
	 * @param name the class name
	 * @param bytes the class's bytes
	 * @return the modified class's bytes
	 */
	public static byte[] modStatic(String name, byte[] bytes)
	{
		return INSTANCE.mod(name, bytes);
	}

	/**
	 * Coremods a class.
	 * 
	 * @param name the class name
	 * @param bytes the class's bytes
	 * @return the modified class's bytes
	 */
	public byte[] mod(final String name, byte[] bytes)
	{
		ClassNode node = ASMUtil.getNode(bytes, 0);
		if (node.visibleAnnotations != null && node.visibleAnnotations.parallelStream().anyMatch(aNode -> aNode.desc.equals("Lfiremerald/mcms/plugin/CoreMod;") || aNode.desc.equals("Lfiremerald/mcms/plugin/CoreModExcluded;"))) return bytes; //do not mod
		List<ICoreModder> modders = new ArrayList<>();
		modders.addAll(globalCoreModders);
		List<ICoreModder> modders2 = coreModders.get(name);
		if (modders2 != null) modders.addAll(modders2);
		if (modders.size() > 0)
		{
			modders.sort(new CoreModderComparator(name));
			for (ICoreModder modder : modders) bytes = modder.coreMod(name, bytes);
		}
		return bytes;
	}
	
	/**
	 * Verifies if a class is a core mod or plugin, and registers it appropriately
	 * 
	 * @param name the class name
	 * @param in the class's data
	 * @param candidate the containing plugin candidate
	 */
	private void validateClass(String name, InputStream in, PluginCandidate candidate)
	{
		try
		{
			ClassReader reader = new ClassReader(in);
			if ((reader.getAccess() & (Opcodes.ACC_ABSTRACT | Opcodes.ACC_INTERFACE)) == 0) //is not an abstract class
			{
				ClassNode node = ASMUtil.getNode(reader.b, 0);
				if (node.visibleAnnotations != null)
				{
					node.visibleAnnotations.forEach(aNode -> {
						if (aNode.desc.equals("Lfiremerald/mcms/plugin/CoreMod;")) coreMods.add(new CoreModWrapper(candidate, name, toMap(aNode.values)));
						else if (aNode.desc.equals("Lfiremerald/mcms/plugin/Plugin;")) plugins.add(new PluginWrapper(candidate, name, toMap(aNode.values)));
					});
				}
			}
		}
		catch (IOException e) {}
	}
	
	/**
	 * Converts a {@link List} from annotation data to a {@link Map}
	 * 
	 * @param values the annotation data
	 * @return a map of the annotation data
	 */
	private Map<String, Object> toMap(List<Object> values)
	{
		Map<String, Object> map = new HashMap<>();
		for (int i = 0; i < values.size(); i += 2) map.put((String) values.get(i), values.get(i + 1));
		return map;
	}
	
	/**
	 * Searches for and registers plugins and core mods from a plugin candidate
	 * 
	 * @param candidate the plugin candidate
	 */
	private void getPlugins(PluginCandidate candidate)
	{
		File file = candidate.file;
		JarFile jarFile = null;
		try
		{
			if (file.isFile())
			{
				jarFile = new JarFile(file);
				Enumeration<JarEntry> enu = jarFile.entries();
				while (enu.hasMoreElements())
				{
				    JarEntry ent = enu.nextElement();
				    if (ent.isDirectory() || !ent.getName().endsWith(".class")) continue;
				    else
				    {
				    	InputStream in = null;
				    	try
				    	{
				    		in = jarFile.getInputStream(ent);
			    			String name = ent.getName();
			    			name = name.substring(0, name.length() - 6).replace('/', '.').replace('\\', '.');
			    			validateClass(name, in, candidate);
				    	}
				    	catch (IOException e)
				    	{
				    		LOGGER.warn("Couldn't analyze " + ent.getName() + " from " + file.getAbsolutePath(), e);
				    	}
				    	if (in != null) try
				    	{
				    		in.close();
				    	}
				    	catch (IOException e)
				    	{
				    		LOGGER.warn("Couldn't close " + ent.getName() + " from " + file.getAbsolutePath(), e);
				    	}
				    }
				}
			}
			else if (file.isDirectory())
			{
				int toRemove = file.getAbsolutePath().length();
				if (toRemove > 0) toRemove++;
				parseFilesForCoremods(toRemove, file, candidate);
			}
			else return;
		}
		catch (IOException e)
		{
			LOGGER.warn("Couldn't open jarfile " + file, e);
		}
		if (jarFile != null) try
		{
			jarFile.close();
		}
		catch (IOException e)
		{
			LOGGER.warn("Couldn't close jarfile " + file, e);
		}
	}

	/**
	 * Iterative method for searching for and registering plugins and core mods from a directory
	 * 
	 * @param toRemove the number of characters to remove from the file path when determining the class name
	 * @param directory the directory to search
	 * @param candidate the plugin candidate
	 */
	private void parseFilesForCoremods(int toRemove, File directory, PluginCandidate candidate)
	{
		for (File file : directory.listFiles())
		{
			if (file.isDirectory()) parseFilesForCoremods(toRemove, file, candidate);
			else if (file.isFile() && file.getName().endsWith(".class"))
			{
		    	InputStream in = null;
		    	try
		    	{
		    		in = new FileInputStream(file);
	    			String name = file.toString();
	    			name = name.substring(toRemove, name.length() - 6).replace('/', '.').replace('\\', '.');
	    			validateClass(name, in, candidate);
		    	}
		    	catch (IOException e)
		    	{
		    		LOGGER.warn("Couldn't analyze " + file.getAbsolutePath(), e);
		    	}
		    	if (in != null) try
		    	{
		    		in.close();
		    	}
		    	catch (IOException e)
		    	{
		    		LOGGER.warn("Couldn't close " + file.getAbsolutePath(), e);
		    	}
			}
		}
	}
	
	/**
	 * Constructs and registers core mods
	 */
	private void constructCoreMods()
	{
		coreMods.forEach(wrapper ->
		{
			try
			{
				ICoreMod coreMod = wrapper.constructCoreMod();
				wrapper.candidate.isPlugin = true;
				if (loadedCoreMods.containsKey(wrapper.id)) throw new IllegalStateException("Duplicate plugin IDs: " + wrapper.id);
				loadedCoreMods.put(wrapper.id, wrapper);
				Map<String, List<ICoreModder>> map;
				if ((map = coreMod.getModders()) != null) map.forEach((className2, list) ->
				{
					if (list != null)
					{
						List<ICoreModder> coreModders = this.coreModders.get(className2);
						if (coreModders == null) this.coreModders.put(className2, coreModders = new ArrayList<>());
						coreModders.addAll(list);					
					}
				});
				List<ICoreModder> list;
				if ((list = coreMod.getGlobalModders()) != null) globalCoreModders.addAll(list);
			}
			catch (ClassNotFoundException e)
			{
				LOGGER.warn("Couldn't find coremod class " + wrapper.className, e);
			}
			catch (NoSuchMethodException e)
			{
				LOGGER.warn("Couldn't instantiate coremod class " + wrapper.className + ": missing public empty constructor.", e);
			}
			catch (IllegalAccessException e)
			{
				LOGGER.warn("Couldn't instantiate coremod class " + wrapper.className + ": empty constructor is not public.", e);
			}
			catch (InstantiationException | IllegalArgumentException | InvocationTargetException | SecurityException e)
			{
				LOGGER.warn("Couldn't instantiate coremod class " + wrapper.className, e);
			}
			catch (Throwable t)
			{
				LOGGER.warn("Error constructing coremod class " + wrapper.className, t);
			}
		});
		coreMods = null;
	}
	
	/**
	 * Constructs and registers plugins
	 */
	public void constructPlugins()
	{
		loadedCoreMods.values().forEach(wrapper -> {
			wrapper.constructPlugin();
			addPlugin(wrapper);
		});
		plugins.forEach(wrapper ->
		{
			try
			{
				wrapper.constructPlugin();
				wrapper.candidate.isPlugin = true;
				addPlugin(wrapper);
			}
			catch (ClassNotFoundException e)
			{
				LOGGER.warn("Couldn't find plugin class " + wrapper.className, e);
			}
			catch (NoSuchMethodException e)
			{
				LOGGER.warn("Couldn't instantiate plugin class " + wrapper.className + ": missing public empty constructor.", e);
			}
			catch (IllegalAccessException e)
			{
				LOGGER.warn("Couldn't instantiate plugin class " + wrapper.className + ": empty constructor is not public.", e);
			}
			catch (InstantiationException | IllegalArgumentException | InvocationTargetException | SecurityException e)
			{
				LOGGER.warn("Couldn't instantiate plugin class " + wrapper.className, e);
			}
			catch (Throwable t)
			{
				LOGGER.warn("Error constructing plugin class " + wrapper.className, t);
			}
		});
		activePlugin = Main.ID;
		plugins = null;
		this.pluginCandidates.forEach((candidate) -> 
		{
			if (!candidate.isPlugin && candidate.file.isFile()) LOGGER.log(Level.INFO, "Found a non-plugin file " + candidate.file.getAbsolutePath() + ". It has still been injected into the classpath.");
		});
	}
	
	/**
	 * Loads plugins and then launches the game
	 * 
	 * @param args the program arguments
	 */
	public static void launchGame(String[] args)
	{
		INSTANCE.getPlugins();
		INSTANCE.constructCoreMods();
		Main.launch(args);
	}
	
	/**
	 * Finds and loads all core mod and plugin wrappers
	 */
	private void getPlugins()
	{
		coreMods = new ArrayList<>();
		plugins = new ArrayList<>();
		URLClassLoader classLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
		findClasspathFiles(classLoader);
		this.pluginCandidates.forEach((candidate) ->
		{
			try
			{
				ADD_URL.invoke(classLoader, candidate.file.toURI().toURL());
			}
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | MalformedURLException e)
			{
				LOGGER.warn("Couldn't add candidate jarfile " + candidate.file + " to classloader", e);
			}
		});
		this.pluginCandidates.forEach((candidate) -> getPlugins(candidate));
		getPlugins(new File("plugins"));
	}
	
	/**
	 * Grabs plugin candidates from a directory 
	 * 
	 * @param directory
	 */
	private void getPlugins(File directory)
	{
		LOGGER.info("Searching for plugins in " + directory.getAbsolutePath());
		directory.mkdirs();
		for (File file : directory.listFiles()) if (!file.isDirectory()) this.pluginCandidates.add(new PluginCandidate(file, false));
	}
	
	/**
	 * registers a plugin and registers any event listeners in the instance using {@link EventBus#registerListeners(Object handler)}
	 * 
	 * @param plugin the plugin wrapper
	 */
	private void addPlugin(AbstractPluginWrapper plugin)
	{
		LOGGER.debug("Adding plugin with id " + plugin.id + " and class " + plugin.className);
		activePlugin = plugin.id;
		loadedPlugins.put(plugin.id, plugin);
		Main.instance.EVENT_BUS.registerListeners(plugin.getPlugin());
	}
	
	/**
	 * The currently active plugin, for plugin registering and event handling
	 */
	public String activePlugin = Main.ID;
	
    /**
     * Known library files to be ignored when searching the classpath for plugin candidates
     */
    private static final List<String> STANDARD_LIBRARIES = Arrays.asList(
    		//JRE libraries
    		"javaagent-shaded.jar",
    	    "resources.jar",
    	    "rt.jar",
    	    "jsse.jar",
    	    "jce.jar",
    	    "charsets.jar",
    	    "jfr.jar",
    	    "access-bridge-64.jar",
    	    "cldrdata.jar",
    	    "dnsns.jar",
    	    "jaccess.jar",
    	    "jfxrt.jar",
    	    "localedata.jar",
    	    "nashorn.jar",
    	    "sunec.jar",
    	    "sunjce_provider.jar",
    	    "sunmscapi.jar",
    	    "sunpkcs11.jar",
    	    "zipfs.jar",
    	    //maven dependencies
    	    "image4j-05ed5bf.jar",
    	    "log4j-core-2.13.3.jar",
    	    "log4j-api-2.13.3.jar",
    	    "lwjgl-3.2.3.jar",
    	    "lwjgl-glfw-3.2.3.jar",
    	    "lwjgl-jawt-3.2.3.jar",
    	    "lwjgl-nfd-3.2.3.jar",
    	    "lwjgl-opengl-3.2.3.jar",
    	    "gson-2.8.5.jar",
    	    "lwjgl3-awt-0.1.1.jar",
    	    "joml-1.9.16.jar",
    	    "asm-6.2.1.jar",
    	    "asm-commons-6.2.1.jar",
    	    "asm-analysis-6.2.1.jar",
    	    "asm-tree-6.2.1.jar",
    	    //natives windows 32-bit
    	    "lwjgl-3.2.3-natives-windows-x86.jar",
    	    "lwjgl-glfw-3.2.3-natives-windows-x86.jar",
    	    "lwjgl-nfd-3.2.3-natives-windows-x86.jar",
    	    "lwjgl-opengl-3.2.3-natives-windows-x86.jar",
    	    //natives windows 64-bit
    	    "lwjgl-3.2.3-natives-windows.jar",
    	    "lwjgl-glfw-3.2.3-natives-windows.jar",
    	    "lwjgl-nfd-3.2.3-natives-windows.jar",
    	    "lwjgl-opengl-3.2.3-natives-windows.jar",
    	    //natives linux
    	    "lwjgl-3.2.3-natives-linux.jar",
    	    "lwjgl-glfw-3.2.3-natives-linux.jar",
    	    "lwjgl-nfd-3.2.3-natives-linux.jar",
    	    "lwjgl-opengl-3.2.3-natives-linux.jar",
    	    //natives macos
    	    "lwjgl-3.2.3-natives-macos.jar",
    	    "lwjgl-glfw-3.2.3-natives-macos.jar",
    	    "lwjgl-nfd-3.2.3-natives-macos.jar",
    	    "lwjgl-opengl-3.2.3-natives-macos.jar",
    	    //Referenced Libraries
    	    "org.eclipse.jdt.annotation_2.2.300.v20190328-1431.jar");

    /**
     * converts an array of {@link URL}'s to an array of {@link File}'s
     * 
     * @param urls the URLS to convert
     * @return the files
     */
    private File[] getParentSources(URL[] urls)
    {
        File[] sources = new File[urls.length];
        try
        {
            for (int i = 0; i < urls.length; i++)
            {
                sources[i] = new File(urls[i].toURI());
            }
            return sources;
        }
        catch (URISyntaxException e)
        {
        	LOGGER.error("Unable to get parent sources", e);
        	throw new IllegalStateException(e);
        }
    }

    /**
     * Searches for plugin candidates from the classpath
     * 
     * @param pluginClassLoader the class loader
     */
    private void findClasspathFiles(URLClassLoader pluginClassLoader)
    {
        File[] sources = getParentSources(pluginClassLoader.getURLs());
        if (sources.length == 1 && sources[0].isFile())
        {
        	LOGGER.info(String.format("Source is a file at %s, loading", sources[0].getAbsolutePath()));
        	this.pluginCandidates.add(new PluginCandidate(sources[0], true));
        }
        else
        {
        	StringBuilder str = new StringBuilder("Found sources:");
        	for (File src : sources)
            {
            	str.append("\n    \"");
                if (src.isFile())
                {
                	str.append(src.getName());
                    if (STANDARD_LIBRARIES.contains(src.getName()))
                    {
                    	LOGGER.debug("Skipping known library file " + src.getAbsolutePath());
                    }
                    else
                    {
                    	LOGGER.info("Found a source related file at " + src.getAbsolutePath());
                    	this.pluginCandidates.add(new PluginCandidate(src, true));
                    }
                }
                else if (src.isDirectory())
                {
                	str.append(src.getAbsolutePath());
                	LOGGER.info("Found a source related directory at " + src.getAbsolutePath());
                	this.pluginCandidates.add(new PluginCandidate(src, true));
                }
            	str.append("\",");
            }
        	LOGGER.debug(str.toString());
        }
    }
}