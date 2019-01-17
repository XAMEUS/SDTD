import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class DateUtils {
    private static SimpleDateFormat ISO8601DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE);

    private final static long DAY_IN_MS = 86400000;

    public static Date getDate(String strDate) throws ParseException {
        return ISO8601DATEFORMAT.parse(strDate);
    }

    public static String getStrDate(Date date) {
        return ISO8601DATEFORMAT.format(date);
    }


    public static List<String> getDatesBetween(String dateStart, String dateEnd) throws ParseException {
        List<String> dates = new LinkedList<>();

        long time = getDate(dateStart).getTime();
        long endTime = getDate(dateEnd).getTime();

        for (; time <= endTime; time += DAY_IN_MS) {
            dates.add(getStrDate(new Date(time)));
        }

        return dates;
    }
}
