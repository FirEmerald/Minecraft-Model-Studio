package firemerald.mcms.launchwrapper;

import firemerald.mcms.launchwrapper.LauncherLogger.Level;
import java.io.PrintStream;

public class PrintStreamLauncherLogger extends PrintStream
{
	public final LauncherLogger logger;
	public final Level level;
	
	public PrintStreamLauncherLogger(PrintStream original, LauncherLogger logger, Level level)
	{
		super(original);
		this.logger = logger;
		this.level = level;
	}
	
	@Override
	public void print(String s)
	{
		this.logger.logRaw(this.level, s);
	}
	
	@Override
	public void print(boolean b)
	{
		this.print(Boolean.toString(b));
	}
	
	@Override
	public void print(char c)
	{
		this.print(Character.toString(c));
	}
	
	@Override
	public void print(char[] c)
	{
		this.print(String.valueOf(c));
	}
	
	@Override
	public void print(double d)
	{
		this.print(Double.toString(d));
	}
	
	@Override
	public void print(float f)
	{
		this.print(Float.toString(f));
	}
	
	@Override
	public void print(int i)
	{
		this.print(Integer.toString(i));
	}
	
	@Override
	public void print(long l)
	{
		this.print(Long.toString(l));
	}
	
	@Override
	public void print(Object o)
	{
		this.print(o == null ? "null" : o.toString());
	}
	
	@Override
	public void println(String s)
	{
		this.print(String.format(logger.name, level.name()) + s + "\n");
	}
	
	@Override
	public void println(boolean b)
	{
		this.println(Boolean.toString(b));
	}
	
	@Override
	public void println(char c)
	{
		this.println(Character.toString(c));
	}
	
	@Override
	public void println(char[] c)
	{
		this.println(String.valueOf(c));
	}
	
	@Override
	public void println(double d)
	{
		this.println(Double.toString(d));
	}
	
	@Override
	public void println(float f)
	{
		this.println(Float.toString(f));
	}
	
	@Override
	public void println(int i)
	{
		this.println(Integer.toString(i));
	}
	
	@Override
	public void println(long l)
	{
		this.println(Long.toString(l));
	}
	
	@Override
	public void println(Object o)
	{
		this.println(o == null ? "null" : o.toString());
	}
}
