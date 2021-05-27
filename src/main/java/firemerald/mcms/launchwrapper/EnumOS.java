package firemerald.mcms.launchwrapper;

import java.util.Locale;

public enum EnumOS
{
	WINDOWS_X86("natives-windows-x86", "F6B9702F507A9E02ABBC0E73D48C26EF", "DE37AA2EF622F54C5FDF5C7DB092A146", "1C7FC204A1DA1B49296D0655F2F085B5", "1F37E2259078F03A761C920E8CA9284A"),
	WINDOWS_X86_64("natives-windows",  "610A4693EB61711CD31EFE3562C40DD7", "307733DDD62A2278A0677A6393BDC3A9", "FD03ED8F3A01F98EB22A65029A408BC3", "334D53905D67FEF58BDB84BF81F28EF8"),
	LINUX_X86_64("natives-linux",      "C0D0CB6AF2B93F987DBF5A661D076B7E", "1C9354D479064C4710F34D277548A21B", "A877CD48DB9FFF8CF4FFD59C99708CF5", "3DF5002E8A108662723A53C52F237ABA"),
	MACOS_X86_64("natives-macos",      "FE96C67EC87EBD692BB2B2B6CFE26731", "3DEB6D6073CABD5E669ADA2D37C449A1", "83D37432D81AD52F342F6E171950A20E", "A727E4FF00C86CDDE7A10E19076EBA34");

	public final String lwjglNatives;
	public final String md5_LWJGL_natvies;
	public final String md5_LWJGL_GLFW_natvies;
	public final String md5_LWJGL_NFD_natvies;
	public final String md5_LWJGL_OpenGL_natvies;
	
	EnumOS(String lwjglNatives, String md5_LWJGL_natvies, String md5_LWJGL_GLFW_natvies, String md5_LWJGL_NFD_natvies, String md5_LWJGL_OpenGL_natvies)
	{
		this.lwjglNatives = lwjglNatives;
		this.md5_LWJGL_natvies = md5_LWJGL_natvies;
		this.md5_LWJGL_GLFW_natvies = md5_LWJGL_GLFW_natvies;
		this.md5_LWJGL_NFD_natvies = md5_LWJGL_NFD_natvies;
		this.md5_LWJGL_OpenGL_natvies = md5_LWJGL_OpenGL_natvies;
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