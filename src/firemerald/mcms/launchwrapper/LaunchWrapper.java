package firemerald.mcms.launchwrapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import firemerald.mcms.launchwrapper.LauncherLogger.Level;

public class LaunchWrapper
{
	public static final LauncherLogger LOGGER;
	
    static
    {
    	LOGGER = new LauncherLogger(new File("logs/launchwrapper.log")); //the main logger
    	System.setOut(new PrintStreamLauncherLogger(System.out, LOGGER, Level.STDOUT)); //replace the default output stream with one that goes to the logger
    	System.setErr(new PrintStreamLauncherLogger(System.err, LOGGER, Level.STDERR)); //replace the default error stream with one that goes to the logger
    }
	
	//TODO wrapper log
	public static void main(String[] args)
	{
		final String lwjglVersion = "3.2.3";
		final EnumOS os = EnumOS.getOS();
		final List<URL> jars = new ArrayList<>();
		jars.add(LaunchWrapper.class.getProtectionDomain().getCodeSource().getLocation()); //add main JAR
		downloadMaven("junit", "junit", "3.8.1", jars, new byte[] {31, 64, -5, 120, 42, 79, 44, -9, -113, 22, 29, 50, 103, 15, 122, 58});
		downloadJar("https://github.com/imcdonagh/image4j/releases/download/0.7.2/image4j-0.7.2.jar", "github/image4j/0.7.2/image4j-0.7.2.jar", jars, new byte[] {64, -116, -4, 64, -45, -112, 16, -20, -46, 64, -114, -11, 18, 71, -20, -116});
		downloadMaven("org.apache.logging.log4j", "log4j-core", "2.11.0", jars, new byte[] {42, -66, -62, -50, 102, 94, 13, 82, -102, 63, 40, -1, -5, -69, 45, -45});
		downloadMaven("org.apache.logging.log4j", "log4j-api", "2.11.0", jars, new byte[] {-91, -127, 96, 15, 48, 16, -4, -65, 13, -101, 61, 73, 7, 20, 35, -107});
		downloadLWJGL("org.lwjgl", "lwjgl", lwjglVersion, os, jars, new byte[] {46, -52, 118, -27, -58, 29, -52, -47, -24, 47, -122, 116, -128, 6, 71, 59}, new byte[] {97, 10, 70, -109, -21, 97, 113, 28, -45, 30, -2, 53, 98, -60, 13, -41});
		downloadLWJGL("org.lwjgl", "lwjgl-glfw", lwjglVersion, os, jars, new byte[] {-114, 70, 111, -41, -87, 97, -74, -96, 2, 82, -71, -15, 98, -45, -1, 99}, new byte[] {48, 119, 51, -35, -42, 42, 34, 120, -96, 103, 122, 99, -109, -67, -61, -87});
		downloadMaven("org.lwjgl", "lwjgl-jawt", lwjglVersion, jars, new byte[] {125, -40, -104, -35, 43, -8, 23, 72, -22, -41, 101, -26, -58, 86, -20, -79});
		downloadLWJGL("org.lwjgl", "lwjgl-nfd", lwjglVersion, os, jars, new byte[] {-50, -59, -72, -16, -106, 92, -90, 9, -13, -27, -118, -61, -32, 67, -35, -98}, new byte[] {-3, 3, -19, -113, 58, 1, -7, -114, -78, 42, 101, 2, -102, 64, -117, -61});
		downloadLWJGL("org.lwjgl", "lwjgl-opengl", lwjglVersion, os, jars, new byte[] {-89, 116, 51, -115, -63, -1, -13, -81, 62, -91, 106, -99, -34, 106, 113, -49}, new byte[] {51, 77, 83, -112, 93, 103, -2, -11, -117, -37, -124, -65, -127, -14, -114, -8});
		downloadMaven("com.google.code.gson", "gson", "2.8.5", jars, new byte[] {8, -111, 4, -53, -112, -40, -76, -31, -86, 0, -79, -11, -6, -17, 7, 66});
		downloadMaven("org.lwjglx", "lwjgl3-awt", "0.1.1", jars, new byte[] {-113, -44, -117, -104, 4, 89, 23, -56, -73, 9, 34, 10, -9, -103, 97, -65});
		downloadMaven("org.joml", "joml", "1.9.16", jars, new byte[] {119, -11, 11, -67, 51, -115, -117, -73, -71, 108, -54, 52, -68, -100, -88, 24});
		@SuppressWarnings("resource") //this classloader is used for the rest of the program so there's no need to close it
		URLClassLoader classLoader = new URLClassLoader(jars.toArray(new URL[jars.size()]), null); //we can't use the system classloader
		LOGGER.info("Launching wrapped MCMS");
		try
		{
			classLoader.loadClass("firemerald.mcms.Main").getMethod("main", String[].class).invoke(null, new Object[] {args}); //Main.main(args) 
		}
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LOGGER.debug("Main thread exit");
	}
	
	public static void downloadLWJGL(String groupId, String artifactId, String version, EnumOS os, List<URL> jars, byte[] md5jar, byte[] md5native)
	{
		downloadMaven(groupId, artifactId, version, jars, md5jar);
		downloadMaven(groupId, artifactId, version, os.lwjglNatives, jars, md5native);
	}
	
	public static void downloadMaven(String groupId, String artifactId, String version, List<URL> jars, byte[] md5)
	{
		downloadMaven(groupId, artifactId, version, null, jars, md5);
	}
	
	public static void downloadMaven(String groupId, String artifactId, String version, String classifier, List<URL> jars, byte[] md5)
	{
		downloadMaven("https://repo1.maven.org/maven2", groupId, artifactId, version, classifier, jars, md5);
	}
	
	public static void downloadMaven(String repository, String groupId, String artifactId, String version, String classifier, List<URL> jars, byte[] md5)
	{
		StringBuilder builder = new StringBuilder(groupId.replace('.', '/')).append('/').append(artifactId).append('/').append(version).append('/').append(artifactId).append('-').append(version);
		if (classifier != null) builder.append('-').append(classifier);
		String file = builder.append(".jar").toString();
		downloadJar(repository + "/" + file, file, jars, md5);
	}
	
	public static void downloadJar(String url, String des, List<URL> jars, byte[] md5)
	{
		boolean download = true;
		byte[] bytes = new byte[4096];
		File file = new File("libs/" + des);
		if (file.exists())
		{
	    	InputStream in = null;
	    	try
	    	{
	    		in = new FileInputStream(file);
				MessageDigest digest = MessageDigest.getInstance("MD5");
	    		int read;
	    		while ((read = in.read(bytes)) > 0) digest.update(bytes, 0, read);
	    		byte[] got = digest.digest();
				//System.out.println(file);
	    		//System.out.println(convertByteArrayToCodeString(got));
	    		if (got.length != md5.length)
	    		{
	    			LOGGER.warn("Invalid MD5 for " + des + " library, was " + convertByteArrayToHexString(got) + ", should be " + convertByteArrayToHexString(md5));
	    		}
	    		else
	    		{
	    			download = false;
	    			for (int i = 0; i < got.length; i++) if (got[i] != md5[i])
	    			{
	    				download = true;
	    				LOGGER.warn("Invalid MD5 for " + des + " library, was " + convertByteArrayToHexString(got) + ", should be " + convertByteArrayToHexString(md5));
		    			break;
	    			}
	    			if (!download) LOGGER.info(des + " MD5 passed");
	    		}
	    	}
	    	catch (IOException | NoSuchAlgorithmException e)
	    	{
	    		LOGGER.warn("Couldn't retrieve " + des + " MD5");
	    		e.printStackTrace();
	    	}
	    	if (in != null) try
	    	{
	    		in.close();
	    	} catch (IOException e) {}
		}
		if (download)
		{
			LOGGER.info("Downloading " + url + " to " + file.getAbsolutePath());
	    	InputStream in = null;
	    	OutputStream out = null;
	    	try
	    	{
	    		URL jomlURL = new URL(url);
	    		file.getParentFile().mkdirs();
	    		file.createNewFile();
	    		in = jomlURL.openStream();
	    		out = new FileOutputStream(file);
	    		int read;
	    		int downloaded = 0;
	    		while ((read = in.read(bytes)) > 0)
	    		{
	    			downloaded += read;
	    			out.write(bytes, 0, read);
	    			LOGGER.debug(downloaded + " bytes");
	    		}
    			LOGGER.info("downloaded " + url + " successfully");
	    	}
	    	catch (Exception e)
	    	{
	    		LOGGER.error("Couldn't download " + des + " library");
	    		e.printStackTrace();
	    	}
	    	if (in != null) try
	    	{
	    		in.close();
	    	} catch (IOException e) {}
	    	if (out != null) try
	    	{
	    		out.close();
	    	} catch (IOException e) {}
		}
		if (file.exists())
		{
			try
			{
				jars.add(file.toURI().toURL());
			}
			catch (MalformedURLException e)
			{
				LOGGER.error("Could not add " + des + " library");
				e.printStackTrace();
			}
		}
	}
	
	private static String convertByteArrayToHexString(byte[] arrayBytes)
	{
	    StringBuffer stringBuffer = new StringBuffer();
	    for (byte b : arrayBytes) stringBuffer.append(Integer.toString(b & 0xff, 16));
	    return stringBuffer.toString();
	}
	/** /
	private static String convertByteArrayToCodeString(byte[] arrayBytes)
	{
		StringJoiner joiner = new StringJoiner(", ");
	    for (byte b : arrayBytes) joiner.add(Byte.toString(b));
	    return "new byte[] {" + joiner.toString() + "}";
	}
	/**/
}