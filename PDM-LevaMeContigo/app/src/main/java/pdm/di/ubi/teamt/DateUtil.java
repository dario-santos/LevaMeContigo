package pdm.di.ubi.teamt;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

public class DateUtil
{
    private DateUtil() {}

    public static long getDateDifferenceInDays(String firstDate, String secondDate)
    {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        long diff = 0;

        try
        {
            long diffInMillies = Math.abs(df.parse(firstDate).getTime() - df.parse(secondDate).getTime());
            diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        }
        catch (Exception e) {}

        return diff;
    }

    public static long getSignDateDifferenceInDays(String firstDate, String secondDate)
    {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        long diff = 0;

        try
        {
            long diffInMillies = Math.abs(df.parse(firstDate).getTime() - df.parse(secondDate).getTime());
            diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

            if(df.parse(secondDate).getTime() - df.parse(firstDate).getTime() < 0)
                diff = diff * -1;
        }
        catch (Exception e) {}

        return diff;
    }
}
