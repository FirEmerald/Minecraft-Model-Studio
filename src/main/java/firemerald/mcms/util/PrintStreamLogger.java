package firemerald.mcms.util;

import java.io.PrintStream;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

public class PrintStreamLogger extends PrintStream
{
	private final Logger logger;
	private final Level level;
	
	public PrintStreamLogger(PrintStream original, Logger logger, Level level)
	{
		super(original);
		this.logger = logger;
		this.level = level;
	}
	
	@Override
	public void println(String s)
	{
		logger.log(level, Thread.currentThread().getStackTrace()[2] + ": " + s);
	}
	
	@Override
	public void println(boolean b)
	{
		logger.log(level, Thread.currentThread().getStackTrace()[2] + ": " + Boolean.toString(b));
	}
	
	@Override
	public void println(char c)
	{
		logger.log(level, Thread.currentThread().getStackTrace()[2] + ": " + Character.toString(c));
	}
	
	@Override
	public void println(char[] c)
	{
		logger.log(level, Thread.currentThread().getStackTrace()[2] + ": " + String.valueOf(c));
	}
	
	@Override
	public void println(double d)
	{
		logger.log(level, Thread.currentThread().getStackTrace()[2] + ": " + Double.toString(d));
	}
	
	@Override
	public void println(float f)
	{
		logger.log(level, Thread.currentThread().getStackTrace()[2] + ": " + Float.toString(f));
	}
	
	@Override
	public void println(int i)
	{
		logger.log(level, Thread.currentThread().getStackTrace()[2] + ": " + Integer.toString(i));
	}
	
	@Override
	public void println(long l)
	{
		logger.log(level, Thread.currentThread().getStackTrace()[2] + ": " + Long.toString(l));
	}
	
	@Override
	public void println(Object o)
	{
		logger.log(level, Thread.currentThread().getStackTrace()[2] + ": " + (o == null ? "null" : o.toString()));
	}
}