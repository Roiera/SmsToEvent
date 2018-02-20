package apps.rachamimr.smstoevent;

import android.util.Log;

import java.util.Calendar;

public class Smile implements MsgParser {
    private static final String TAG = "SmileParser";
    /* return if the message should be parsed by this parser */
    public boolean shouldHandleMsg(String msg) {
        return msg.contains("סמייל");
    }

    /* Parses the message */
    public EventInfo parseMsg(String msg, Calendar receivedDate) {
        String[] words = msg.split(",");

        if (words.length < 4) {
            return null;
        }

        StringBuilder location = new StringBuilder();
        String[] addressStr = words[1].split(" ");
        for (int i = 4; i < addressStr.length; i++) {
            location.append(addressStr[i]);
            location.append(" ");
        }
        location.append(words[2]);

        String[] dateStr = words[3].split(" ");
        String[] date = dateStr[6].split("/");
        if (date.length < 2) {
            return null;
        }

        String title = "כללית סמייל";
        int day = 0;
        int month = 0;
        int hour = 0;
        int minute = 0;

        String[] time = dateStr[8].split(":");

        try {
            day = Integer.parseInt(date[0]);
            month = Integer.parseInt(date[1]);
            hour = Integer.parseInt(time[0]);
            minute = Integer.parseInt(time[1].substring(0, 1));
        } catch (NumberFormatException e) {
            Log.e(TAG, "Caught NumberFormatException: " + e.getMessage());
        }

        return  new EventInfo(month, day, hour, minute, location.toString(), title,
                "Doctor Visit", receivedDate);
    }
}
