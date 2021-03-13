package Stratonet.Infrastructure.Helpers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateValidator
{
    public static boolean isValidDate(String date)
    {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.setLenient(false);

        try
        {
            format.parse(date);
        }
        catch (ParseException ex)
        {
            return false;
        }

        return true;
    }
}
