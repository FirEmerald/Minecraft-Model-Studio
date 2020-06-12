package firemerald.mcms.util;

public class SystemUtil
{
    private static final Runtime RUNTIME = Runtime.getRuntime();

    public static String OSname()
    {
        return System.getProperty("os.name");
    }

    public static String OSversion() {
        return System.getProperty("os.version");
    }

    public static String OsArch()
    {
        return System.getProperty("os.arch");
    }

    public static long totalMem()
    {
        return RUNTIME.totalMemory();
    }

    public static long usedMem()
    {
        return RUNTIME.totalMemory() - RUNTIME.freeMemory();
    }
}