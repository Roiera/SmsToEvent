package apps.rachamimr.smstoevent;

import java.util.Calendar;
import java.util.Locale;

public class EventInfo {
    int year;
    int month;
    int day;
    int hour;
    int minute;
    String location;
    String title;
    String description;

    private int getYear(int month, int day, Calendar receivedDate) {
        int year = receivedDate.get(Calendar.YEAR);
        int receivedMonth = receivedDate.get(Calendar.MONTH);
        int receivedDay = receivedDate.get(Calendar.DAY_OF_MONTH);

        if ((receivedMonth > month) ||  ((receivedMonth == month) && receivedDay < day)) {
            return year + 1;
        }
        return year;
    }

    @Override
    public String toString() {
        return String.format(Locale.US,"Title: %s\n When: %d/%d/%d %02d:%02d\n Location : %s",
                title, day, month, year, hour, minute, location);
    }

    public EventInfo(int month,
                     int day,
                     int hour,
                     int minute,
                     String location,
                     String title,
                     String description,
                     Calendar receivedDate) {
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.location = location;
        this.title = title;
        this.description = description;
        this.year = getYear(month, day, receivedDate);

    }
}