package firemerald.mcms;

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
		logger.log(level, s);
	}
	
	@Override
	public void println(boolean b)
	{
		logger.log(level, Boolean.toString(b));
	}
	
	@Override
	public void println(char c)
	{
		logger.log(level, Character.toString(c));
	}
	
	@Override
	public void println(char[] c)
	{
		logger.log(level, String.valueOf(c));
	}
	
	@Override
	public void println(double d)
	{
		logger.log(level, Double.toString(d));
	}
	
	@Override
	public void println(float f)
	{
		logger.log(level, Float.toString(f));
	}
	
	@Override
	public void println(int i)
	{
		logger.log(level, Integer.toString(i));
	}
	
	@Override
	public void println(long l)
	{
		logger.log(level, Long.toString(l));
	}
	
	@Override
	public void println(Object o)
	{
		logger.log(level, o == null ? "null" : o.toString());
	}
}