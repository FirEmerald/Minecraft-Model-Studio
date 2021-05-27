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
import java.util.function.Consumer;

public class LaunchWrapper
{
	public static final PrintStream OUT = System.out, ERR = System.err;
	public static final LauncherLogger LOGGER = new LauncherLogger("[LaunchWrapper - %s] ", new File("logs/launchwrapper.log"), OUT, ERR);
	private static ProgressBars progressBars;
	private static int item;
	private static boolean isMDK = false;
	public static final byte[] 
			MD5_JUNIT =                fromHex("1F40FB782A4F2CF78F161D32670F7A3A"),
			MD5_IMAGE4J =              fromHex("408cfc40d39010ecd2408ef51247ec8c"),
			MD5_LOG4J_CORE =           fromHex("CC7D55ED69CC5FD34035B15C6EDF79A0"),
			MD5_LOG4J_API =            fromHex("236B9969DF6B394E88283A9F813B9B95"),
			MD5_LWJGL =                fromHex("2ECC76E5C61DCCD1E82F86748006473B"),
			MD5_LWJGL_GLFW =           fromHex("8E466FD7A961B6A00252B9F162D3FF63"),
			MD5_LWJGL_JAWT =           fromHex("7DD898DD2BF81748EAD765E6C656ECB1"),
			MD5_LWJGL_NFD =            fromHex("CEC5B8F0965CA609F3E58AC3E043DD9E"),
			MD5_LWJGL_OPENGL =         fromHex("A774338DC1FFF3AF3EA56A9DDE6A71CF"),
			MD5_GSON =                 fromHex("089104CB90D8B4E1AA00B1F5FAEF0742"),
			MD5_LWJGL3_AWT =           fromHex("2E39DBCACEC97E60ED8138477CB368D6"),
			MD5_JOML =                 fromHex("2F245D9F01C38A9E42668A203D48AB4D"),
			MD5_ASM =                  fromHex("13AD7C0B2CCE789FF54ABAC0457A481D"),
			MD5_ASM_COMMONS =          fromHex("E62250EFF80324EB575B904E51960072"),
			MD5_ASML_ANALYSIS =        fromHex("1FD0C7DD40CDEA384705E27846DCA6CB"),
			MD5_ASM_TREE =             fromHex("C6C29DB770E61DEFDE1BC14FD385F770"),
			MD5_LWJGL_NATIVES =        fromHex(EnumOS.getOS().md5_LWJGL_natvies),
			MD5_LWJGL_GLFW_NATIVES =   fromHex(EnumOS.getOS().md5_LWJGL_GLFW_natvies),
			MD5_LWJGL_NFD_NATIVES =    fromHex(EnumOS.getOS().md5_LWJGL_NFD_natvies),
			MD5_LWJGL_OPENGL_NATIVES = fromHex(EnumOS.getOS().md5_LWJGL_OpenGL_natvies);
	
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
			downloadMaven("junit", "junit", "3.8.1", jars, MD5_JUNIT);
			downloadJar("https://github.com/imcdonagh/image4j/releases/download/0.7.2/image4j-0.7.2.jar", "github/image4j/0.7.2/image4j-0.7.2.jar", jars, MD5_IMAGE4J);
			downloadMaven("org.apache.logging.log4j", "log4j-core", log4jVersion, jars, MD5_LOG4J_CORE);
			downloadMaven("org.apache.logging.log4j", "log4j-api", log4jVersion, jars, MD5_LOG4J_API);
			downloadLWJGL("org.lwjgl", "lwjgl", lwjglVersion, os, jars, MD5_LWJGL, MD5_LWJGL_NATIVES);
			downloadLWJGL("org.lwjgl", "lwjgl-glfw", lwjglVersion, os, jars, MD5_LWJGL_GLFW, MD5_LWJGL_GLFW_NATIVES);
			downloadMaven("org.lwjgl", "lwjgl-jawt", lwjglVersion, jars, MD5_LWJGL_JAWT);
			downloadLWJGL("org.lwjgl", "lwjgl-nfd", lwjglVersion, os, jars, MD5_LWJGL_NFD, MD5_LWJGL_NFD_NATIVES);
			downloadLWJGL("org.lwjgl", "lwjgl-opengl", lwjglVersion, os, jars, MD5_LWJGL_OPENGL, MD5_LWJGL_OPENGL_NATIVES);
			downloadMaven("com.google.code.gson", "gson", "2.8.5", jars, MD5_GSON);
			downloadMaven("org.lwjglx", "lwjgl3-awt", "0.1.7", jars, MD5_LWJGL3_AWT);
			downloadMaven("org.joml", "joml", "1.9.25", jars, MD5_JOML);
			downloadMaven("org.ow2.asm", "asm", objectWebASMVersion, jars, MD5_ASM);
			downloadMaven("org.ow2.asm", "asm-commons", objectWebASMVersion, jars, MD5_ASM_COMMONS);
			downloadMaven("org.ow2.asm", "asm-analysis", objectWebASMVersion, jars, MD5_ASML_ANALYSIS);
			downloadMaven("org.ow2.asm", "asm-tree", objectWebASMVersion, jars, MD5_ASM_TREE);
			progressBars.dispose();
			progressBars = null;
		}
		else LOGGER.info("MDK detected.");
		CoreModdingClassLoader classLoader = new CoreModdingClassLoader(jars.toArray(new URL[jars.size()]));
		LOGGER.info("Launching wrapped MCMS");
		try
		{
			classLoader.loadClass("firemerald.mcms.plugin.PluginLoader").getMethod("launchGame", String[].class, Consumer.class).invoke(null, new Object[] {args, (Consumer<URL>) classLoader::addURL});
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
				LOGGER.debug(file + " MD5: " + convertByteArrayToHexString(got));
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
				LOGGER.debug(url + " MD5: " + convertByteArrayToHexString(got));
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
	
	protected static byte[] fromHex(String string)
	{
		byte[] vals = new byte[(string.length() + 1) / 2];
		for (int i = 0; i < vals.length; i++) vals[i] = (byte) Integer.parseInt(string.substring(i * 2, Math.min(i * 2 + 2, string.length())), 16);
		return vals;
	}
}