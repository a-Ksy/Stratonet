package Stratonet.Infrastructure.Helpers;

public class FileNameGenerator
{
    public static String Generate(String fileFormat)
    {
        return System.currentTimeMillis() + "." + fileFormat;
    }
}
