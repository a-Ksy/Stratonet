package Stratonet.Infrastructure.Utils;

public class FileNameGenerator
{
    public static String Generate(String fileFormat)
    {
        return System.currentTimeMillis() + "." + fileFormat;
    }
}
