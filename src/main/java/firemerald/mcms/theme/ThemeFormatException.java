package firemerald.mcms.theme;

public class ThemeFormatException extends Exception
{
	private static final long serialVersionUID = 2087036201075342812L;
	
	public ThemeFormatException()
	{
		super();
	}
	
	public ThemeFormatException(String message)
	{
		super(message);
	}
	
	public ThemeFormatException(Throwable cause)
	{
		super(cause);
	}
	
	public ThemeFormatException(String message, Throwable cause)
	{
		super(message, cause);
	}
}