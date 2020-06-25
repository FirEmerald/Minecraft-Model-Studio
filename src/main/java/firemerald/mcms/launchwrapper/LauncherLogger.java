package firemerald.mcms.launchwrapper;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

public class LauncherLogger
{
	public final Path logFile;
	public final PrintStream out;
	public final PrintStream err;
	public final Map<Level, PrintStreamLauncherLogger> wrappers = new HashMap<>();
	
	public LauncherLogger(File logFile)
	{
		logFile.getParentFile().mkdirs();
		this.logFile = logFile.toPath();
		if (logFile.exists()) logFile.delete();
		try
		{
			logFile.createNewFile();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		this.out = System.out;
		this.err = System.err;
		for (Level level : Level.values()) wrappers.put(level, new PrintStreamLauncherLogger(out, this, level));
	}
	
	public void info(String message)
	{
		this.log(Level.INFO, message);
	}
	
	public void warn(String message)
	{
		this.log(Level.WARN, message);
	}
	
	public void error(String message)
	{
		this.log(Level.ERROR, message);
	}
	
	public void fatal(String message)
	{
		this.log(Level.FATAL, message);
	}
	
	public void debug(String message)
	{
		this.log(Level.DEBUG, message);
	}
	
	public void log(Level level, String message)
	{
		this.logRaw(level, "[LaunchWrapper - " + level.name() + "] " + message + "\n");
	}
	
	public void info(String message, Throwable t)
	{
		this.log(Level.INFO, message, t);
	}
	
	public void warn(String message, Throwable t)
	{
		this.log(Level.WARN, message, t);
	}
	
	public void error(String message, Throwable t)
	{
		this.log(Level.ERROR, message, t);
	}
	
	public void fatal(String message, Throwable t)
	{
		this.log(Level.FATAL, message, t);
	}
	
	public void debug(String message, Throwable t)
	{
		this.log(Level.DEBUG, message, t);
	}
	
	public void log(Level level, String message, Throwable t)
	{
		this.logRaw(level, "[LaunchWrapper - " + level.name() + "] " + message + "\n");
		t.printStackTrace(wrappers.get(level));
	}
	
	public void logRaw(Level level, String message)
	{
		try
		{
			Files.write(this.logFile, message.getBytes(), StandardOpenOption.APPEND);
		}
		catch (IOException var4) {}
		if (level.isOut) this.out.print(message);
		if (level.isErr) this.err.print(message);
	}
	
	public static enum Level
	{
		STDOUT(true, false),
		INFO(true, false),
		WARN(false, true),
		STDERR(false, true),
		ERROR(false, true),
		FATAL(false, true),
		DEBUG(false, false);
		
		public final boolean isOut;
		public final boolean isErr;
		
		Level(boolean isOut, boolean isErr)
		{
			this.isOut = isOut;
			this.isErr = isErr;
		}
	}
}