package firemerald.mcms.launchwrapper;

import firemerald.mcms.launchwrapper.LauncherLogger.Level;
import firemerald.mcms.plugin.CoreModdingClassLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

public class LaunchWrapper
{
	public static final PrintStream OUT = System.out, ERR = System.err;
	public static final LauncherLogger LOGGER = new LauncherLogger("[LaunchWrapper - %s] ", new File("logs/launchwrapper.log"), OUT, ERR);
	private static ProgressBars progressBars;
	private static int item;
	private static boolean isMDK = false;
	
	static
	{
		System.setOut(new PrintStreamLauncherLogger(OUT, LOGGER, Level.STDOUT));
		System.setErr(new PrintStreamLauncherLogger(ERR, LOGGER, Level.STDERR));
	}
	
	public static void main(String[] args)
	{
		String val = System.getProperty("isMDK");
		isMDK = val != null && val.equalsIgnoreCase("true");
		List<URL> jars = new ArrayList<>();
		for (String source : System.getProperty("java.class.path").split(System.getProperty("path.separator")))
		{
			try
			{
				jars.add(new File(source).toURI().toURL());
			}
			catch (MalformedURLException e)
			{
				LOGGER.fatal("Couldn't grab classpath source " + source + " for coremodding, this may cause issues and/or crashes", e);
			}
		}
		if (!isMDK)
		{
			progressBars = new ProgressBars("Verifying libraries", 20);
			final String lwjglVersion = "3.2.3";
			final String log4jVersion = "2.13.3";
			final String objectWebASMVersion = "6.2.1";
			EnumOS os = EnumOS.getOS();
			downloadMaven("junit", "junit", "3.8.1", jars, new byte[]{31, 64, -5, 120, 42, 79, 44, -9, -113, 22, 29, 50, 103, 15, 122, 58});
			downloadJar("https://github.com/imcdonagh/image4j/releases/download/0.7.2/image4j-0.7.2.jar", "github/image4j/0.7.2/image4j-0.7.2.jar", jars, new byte[]{64, -116, -4, 64, -45, -112, 16, -20, -46, 64, -114, -11, 18, 71, -20, -116});
			downloadMaven("org.apache.logging.log4j", "log4j-core", log4jVersion, jars, new byte[]{-52, 125, 85, -19, 105, -52, 95, -45, 64, 53, -79, 92, 110, -33, 121, -96});
			downloadMaven("org.apache.logging.log4j", "log4j-api", log4jVersion, jars, new byte[]{35, 107, -103, 105, -33, 107, 57, 78, -120, 40, 58, -97, -127, 59, -101, -107});
			downloadLWJGL("org.lwjgl", "lwjgl", lwjglVersion, os, jars, new byte[]{46, -52, 118, -27, -58, 29, -52, -47, -24, 47, -122, 116, -128, 6, 71, 59}, new byte[]{97, 10, 70, -109, -21, 97, 113, 28, -45, 30, -2, 53, 98, -60, 13, -41});
			downloadLWJGL("org.lwjgl", "lwjgl-glfw", lwjglVersion, os, jars, new byte[]{-114, 70, 111, -41, -87, 97, -74, -96, 2, 82, -71, -15, 98, -45, -1, 99}, new byte[]{48, 119, 51, -35, -42, 42, 34, 120, -96, 103, 122, 99, -109, -67, -61, -87});
			downloadMaven("org.lwjgl", "lwjgl-jawt", lwjglVersion, jars, new byte[]{125, -40, -104, -35, 43, -8, 23, 72, -22, -41, 101, -26, -58, 86, -20, -79});
			downloadLWJGL("org.lwjgl", "lwjgl-nfd", lwjglVersion, os, jars, new byte[]{-50, -59, -72, -16, -106, 92, -90, 9, -13, -27, -118, -61, -32, 67, -35, -98}, new byte[]{-3, 3, -19, -113, 58, 1, -7, -114, -78, 42, 101, 2, -102, 64, -117, -61});
			downloadLWJGL("org.lwjgl", "lwjgl-opengl", lwjglVersion, os, jars, new byte[]{-89, 116, 51, -115, -63, -1, -13, -81, 62, -91, 106, -99, -34, 106, 113, -49}, new byte[]{51, 77, 83, -112, 93, 103, -2, -11, -117, -37, -124, -65, -127, -14, -114, -8});
			downloadMaven("com.google.code.gson", "gson", "2.8.5", jars, new byte[]{8, -111, 4, -53, -112, -40, -76, -31, -86, 0, -79, -11, -6, -17, 7, 66});
			downloadMaven("org.lwjglx", "lwjgl3-awt", "0.1.1", jars, new byte[]{-113, -44, -117, -104, 4, 89, 23, -56, -73, 9, 34, 10, -9, -103, 97, -65});
			downloadMaven("org.joml", "joml", "1.9.16", jars, new byte[]{119, -11, 11, -67, 51, -115, -117, -73, -71, 108, -54, 52, -68, -100, -88, 24});
			downloadMaven("org.ow2.asm", "asm", objectWebASMVersion, jars, new byte[]{19, -83, 124, 11, 44, -50, 120, -97, -11, 74, -70, -64, 69, 122, 72, 29});
			downloadMaven("org.ow2.asm", "asm-commons", objectWebASMVersion, jars, new byte[]{-26, 34, 80, -17, -8, 3, 36, -21, 87, 91, -112, 78, 81, -106, 0, 114});
			downloadMaven("org.ow2.asm", "asm-analysis", objectWebASMVersion, jars, new byte[]{31, -48, -57, -35, 64, -51, -22, 56, 71, 5, -30, 120, 70, -36, -90, -53});
			downloadMaven("org.ow2.asm", "asm-tree", objectWebASMVersion, jars, new byte[]{-58, -62, -99, -73, 112, -26, 29, -17, -34, 27, -63, 79, -45, -123, -9, 112});
			progressBars.dispose();
			progressBars = null;
		}
		else LOGGER.info("MDK detected.");
		CoreModdingClassLoader classLoader = new CoreModdingClassLoader(jars.toArray(new URL[jars.size()]));
		LOGGER.info("Launching wrapped MCMS");
		try
		{
			classLoader.loadClass("firemerald.mcms.plugin.PluginLoader").getMethod("launchGame", String[].class).invoke(null, new Object[] {args});
		}
		catch (Throwable e)
		{
			LOGGER.fatal("MCMS crashed", e);
			Date date = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
			LauncherLogger crashLogger = new LauncherLogger("", new File("crash-logs/crash-" + formatter.format(date) + ".log"), OUT, ERR);
			System.setOut(new PrintStreamLauncherLogger(OUT, crashLogger, Level.STDOUT));
			System.setErr(new PrintStreamLauncherLogger(ERR, crashLogger, Level.STDERR));
			crashLogger.fatal(e);
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
		downloadMaven(groupId, artifactId, version, (String)null, jars, md5);
	}
	
	public static void downloadMaven(String groupId, String artifactId, String version, String classifier, List<URL> jars, byte[] md5)
	{
		downloadMaven("https://repo1.maven.org/maven2", groupId, artifactId, version, classifier, jars, md5);
	}
	
	public static void downloadMaven(String repository, String groupId, String artifactId, String version, String classifier, List<URL> jars, byte[] md5)
	{
		StringBuilder builder = (new StringBuilder(groupId.replace('.', '/'))).append('/').append(artifactId).append('/').append(version).append('/').append(artifactId).append('-').append(version);
		if (classifier != null) builder.append('-').append(classifier);
		String file = builder.append(".jar").toString();
		downloadJar(repository + "/" + file, file, jars, md5);
	}
	
	public static void downloadJar(String url, String des, List<URL> jars, byte[] md5)
	{
		boolean download = true;
		byte[] bytes = new byte[256 * 1024];
		File file = new File("libs/" + des);
		int read;
		if (file.exists())
		{
			progressBars.setProgress("Verifying " + file, item);
			FileInputStream in = null;
			try
			{
				in = new FileInputStream(file);
				MessageDigest digest = MessageDigest.getInstance("MD5");
				int total = 0;
				int size = (int) file.length();
				progressBars.setSecondaryMax(size);
				progressBars.setSubProgress("0%", 0);
				while((read = in.read(bytes)) > 0)
				{
					total += read;
					progressBars.setSubProgress((100 * total / size) + "%", total);
					digest.update(bytes, 0, read);
				}
				byte[] got = digest.digest();
				LOGGER.debug(file + " MD5: " + convertByteArrayToString(got));
				if (got.length != md5.length) LOGGER.warn("Invalid MD5 for " + des + " library, was " + convertByteArrayToHexString(got) + ", should be " + convertByteArrayToHexString(md5));
				else
				{
					download = false;
					for(int i = 0; i < got.length; i++)
					{
						if (got[i] != md5[i])
						{
							download = true;
							LOGGER.warn("Invalid MD5 for " + des + " library, was " + convertByteArrayToHexString(got) + ", should be " + convertByteArrayToHexString(md5));
							break;
						}
					}
					if (!download) LOGGER.info(des + " MD5 passed");
				}
			}
			catch (NoSuchAlgorithmException | IOException e)
			{
				LOGGER.warn("Couldn't retrieve " + des + " MD5", e);
			}
			if (in != null) try
			{
				in.close();
			}
			catch (IOException var16) {}
		}
		if (download)
		{
			progressBars.setProgress("Downloading " + url, item);
			LOGGER.info("Downloading " + url + " to " + file.getAbsolutePath());
			InputStream in = null;
			FileOutputStream out = null;
			try
			{
				URL jomlURL = new URL(url);
				int size;
				try
				{
					size = getFileSize(jomlURL);
				}
				catch (IOException e)
				{
					size = -1;
					LOGGER.warn("Couldn't grab file size of " + url, e);
				}
				progressBars.setSecondaryMax(size);
				file.getParentFile().mkdirs();
				file.createNewFile();
				in = jomlURL.openStream();
				MessageDigest digest = MessageDigest.getInstance("MD5");
				out = new FileOutputStream(file);
				int downloaded = 0;
				if (size > 0) progressBars.setSubProgress("0%", 0);
				else progressBars.setSubProgress("0 bytes", 0);
				while((read = in.read(bytes)) > 0)
				{
					downloaded += read;
					if (size > 0) progressBars.setSubProgress((100 * downloaded / size) + "%", downloaded);
					else progressBars.setSubProgress(downloaded + " bytes", downloaded);
					out.write(bytes, 0, read);
					digest.update(bytes, 0, read);
					//LOGGER.debug(downloaded + " bytes");
				}
				LOGGER.info("downloaded " + url + " successfully");
				byte[] got = digest.digest();
				LOGGER.debug(url + " MD5: " + convertByteArrayToString(got));
				if (got.length != md5.length) LOGGER.warn("Invalid MD5 for " + des + " library, was " + convertByteArrayToHexString(got) + ", should be " + convertByteArrayToHexString(md5));
				else
				{
					download = false;
					for(int i = 0; i < got.length; i++)
					{
						if (got[i] != md5[i])
						{
							download = true;
							LOGGER.warn("Invalid MD5 for " + des + " library, was " + convertByteArrayToHexString(got) + ", should be " + convertByteArrayToHexString(md5));
							break;
						}
					}
					if (!download) LOGGER.info(des + " MD5 passed");
				}
			}
			catch (Exception e)
			{
				LOGGER.error("Couldn't download " + des + " library", e);
			}
			if (in != null) try
			{
				in.close();
			}
			catch (IOException E) {}
			if (out != null) try
			{
				out.close();
			}
			catch (IOException E) {}
		}
		if (file.exists()) try
		{
			jars.add(file.toURI().toURL());
		}
		catch (MalformedURLException E)
		{
			LOGGER.error("Could not add " + des + " library", E);
		}
		item++;
		progressBars.setProgress("Downloaded " + url, item);
	}
	
	private static int getFileSize(URL url) throws IOException
	{
		URLConnection conn = null;
		try
		{
			conn = url.openConnection();
			if (conn instanceof HttpURLConnection) ((HttpURLConnection) conn).setRequestMethod("HEAD");
			conn.getInputStream();
			return conn.getContentLength();
		}
		catch (IOException e)
		{
			throw e;
		}
		finally
		{
			if (conn instanceof HttpURLConnection)((HttpURLConnection) conn).disconnect();
		}
	}
	
	private static String convertByteArrayToHexString(byte[] arrayBytes)
	{
		StringBuffer stringBuffer = new StringBuffer();
		for (byte b : arrayBytes) stringBuffer.append(Integer.toString(b & 255, 16));
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
	private static String convertByteArrayToString(byte[] arrayBytes)
	{
		StringJoiner joiner = new StringJoiner(", ");
	    for (byte b : arrayBytes) joiner.add(Byte.toString(b));
	    return joiner.toString();
	}
}