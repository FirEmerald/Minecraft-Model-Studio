package firemerald.mcms.launchwrapper;

import java.io.PrintStream;

import firemerald.mcms.launchwrapper.LauncherLogger.Level;

public class PrintStreamLauncherLogger extends PrintStream
{
	public final PrintStream original;
	public final LauncherLogger logger;
	public final Level level;
	
	public PrintStreamLauncherLogger(PrintStream original, LauncherLogger logger, Level level)
	{
		super(original);
		this.original = original;
		this.logger = logger;
		this.level = level;
	}
	
	@Override
	public void print(String s)
	{
		logger.logRaw(level, s);
	}
	
	@Override
	public void print(boolean b)
	{
		print(Boolean.toString(b));
	}
	
	@Override
	public void print(char c)
	{
		print(Character.toString(c));
	}
	
	@Override
	public void print(char[] c)
	{
		print(String.valueOf(c));
	}
	
	@Override
	public void print(double d)
	{
		print(Double.toString(d));
	}
	
	@Override
	public void print(float f)
	{
		print(Float.toString(f));
	}
	
	@Override
	public void print(int i)
	{
		print(Integer.toString(i));
	}
	
	@Override
	public void print(long l)
	{
		print(Long.toString(l));
	}
	
	@Override
	public void print(Object o)
	{
		print(o == null ? "null" : o.toString());
	}
	
	@Override
	public void println(String s)
	{
		print("[" + level.name() + "] " + s + "\n");
	}
	
	@Override
	public void println(boolean b)
	{
		println(Boolean.toString(b));
	}
	
	@Override
	public void println(char c)
	{
		println(Character.toString(c));
	}
	
	@Override
	public void println(char[] c)
	{
		println(String.valueOf(c));
	}
	
	@Override
	public void println(double d)
	{
		println(Double.toString(d));
	}
	
	@Override
	public void println(float f)
	{
		println(Float.toString(f));
	}
	
	@Override
	public void println(int i)
	{
		println(Integer.toString(i));
	}
	
	@Override
	public void println(long l)
	{
		println(Long.toString(l));
	}
	
	@Override
	public void println(Object o)
	{
		println(o == null ? "null" : o.toString());
	}
}