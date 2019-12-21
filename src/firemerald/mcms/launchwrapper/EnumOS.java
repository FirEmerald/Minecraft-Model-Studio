package firemerald.mcms.launchwrapper;

import java.util.Locale;

public enum EnumOS
{
	WINDOWS_X86("natives-windows-x86"),
	WINDOWS_X86_64("natives-windows"),
	LINUX_X86_64("natives-linux"),
	//LINUX_ARM32("natives-linux-arm32"),
	//LINUX_ARM64("natives-linux-arm64"),
	MACOS_X86_64("natives-macos");
	
	public final String lwjglNatives;
	
	EnumOS(String lwjglNatives)
	{
		this.lwjglNatives = lwjglNatives;
	}
	
	private static EnumOS theOS;
	
	public static EnumOS getOS()
	{
		if (theOS == null) //get operating system
		{
            String operSys = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
            String operArch = System.getProperty("os.arch").toLowerCase(Locale.ENGLISH);
            //System.out.println(operArch);
            //TODO detect invalid architectures
            if (operSys.contains("mac") || operSys.contains("darwin"))
            {
            	theOS = MACOS_X86_64;
            }
            else if (operSys.contains("win"))
            {
            	//get if x86 or x86_64
            	if (operArch.equals("x86")) theOS = WINDOWS_X86;
            	else theOS = WINDOWS_X86_64;
            }
            else if (operSys.contains("nix") || operSys.contains("nux") || operSys.contains("aix"))
            {
            	//get if x86_64, ARM32, or ARM64 (ARM not supported due to nfd)
            	theOS = LINUX_X86_64;
            }
            else
            {
            	//TODO error dialogue
            	throw new IllegalStateException("Unsupported OS: " + operSys);
            }
		}
		return theOS;
	}
}