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
	
	public void print(String s)
	{
		this.logger.logRaw(this.level, s);
	}
	
	public void print(boolean b)
	{
		this.print(Boolean.toString(b));
	}
	
	public void print(char c)
	{
		this.print(Character.toString(c));
	}
	
	public void print(char[] c)
	{
		this.print(String.valueOf(c));
	}
	
	public void print(double d)
	{
		this.print(Double.toString(d));
	}
	
	public void print(float f)
	{
		this.print(Float.toString(f));
	}
	
	public void print(int i)
	{
		this.print(Integer.toString(i));
	}
	
	public void print(long l)
	{
		this.print(Long.toString(l));
	}
	
	public void print(Object o)
	{
		this.print(o == null ? "null" : o.toString());
	}
	
	public void println(String s)
	{
		this.print("[LaunchWrapper - " + this.level.name() + "] " + s + "\n");
	}
	
	public void println(boolean b)
	{
		this.println(Boolean.toString(b));
	}
	
	public void println(char c)
	{
		this.println(Character.toString(c));
	}
	
	public void println(char[] c)
	{
		this.println(String.valueOf(c));
	}
	
	public void println(double d)
	{
		this.println(Double.toString(d));
	}
	
	public void println(float f)
	{
		this.println(Float.toString(f));
	}
	
	public void println(int i)
	{
		this.println(Integer.toString(i));
	}
	
	public void println(long l)
	{
		this.println(Long.toString(l));
	}
	
	public void println(Object o)
	{
		this.println(o == null ? "null" : o.toString());
	}
}
