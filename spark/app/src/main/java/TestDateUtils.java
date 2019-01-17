import java.text.ParseException;
import java.util.Date;
import java.util.List;

public class TestDateUtils {
    public static void main(String args[]) {
        try {
            Date d = DateUtils.getDate("2042-12-31");
            System.out.println(d);
            String s = DateUtils.getStrDate(d);
            System.out.println(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            List<String> dates = DateUtils.getDatesBetween("2015-01-01", "2018-03-06");

            for (String date : dates) {
                System.out.println(date);
            }

            System.out.println(dates.size());

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
