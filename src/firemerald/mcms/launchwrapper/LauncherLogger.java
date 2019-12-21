package firemerald.mcms.launchwrapper;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class LauncherLogger
{
	public final Path logFile;
	public final PrintStream out, err;
	
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
		out = System.out;
		err = System.err;
	}
	
	public void info(String message)
	{
		log(Level.INFO, message);
	}
	
	public void warn(String message)
	{
		log(Level.WARN, message);
	}
	
	public void error(String message)
	{
		log(Level.ERROR, message);
	}
	
	public void debug(String message)
	{
		log(Level.DEBUG, message);
	}
	
	public void log(Level level, String message)
	{
		logRaw(level, "[" + level.name() + "] " + message + "\n");
	}
	
	public void logRaw(Level level, String message)
	{
		try
		{
			Files.write(logFile, message.getBytes(), StandardOpenOption.APPEND);
		}
		catch (IOException e) {}
		if (level.isOut) out.print(message);
		if (level.isErr) err.print(message);
	}
	
	static enum Level
	{
		STDOUT(true, false),
		INFO(true, false),
		WARN(false, true),
		STDERR(false, true),
		ERROR(false, true),
		DEBUG(false, false);
		
		public final boolean isOut, isErr;
		
		Level(boolean isOut, boolean isErr)
		{
			this.isOut = isOut;
			this.isErr = isErr;
		}
	}
}