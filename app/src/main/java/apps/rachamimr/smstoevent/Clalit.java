package apps.rachamimr.smstoevent;

import android.util.Log;

import java.util.Calendar;

public class Clalit implements MsgParser {
    private static final String TAG = "ClalitParser";
    /* return if the message should be parsed by this parser */
    public boolean shouldHandleMsg(String msg) {
        return msg.contains("נקבע לך תור לדר'");
    }

    /* Parses the message */
    public EventInfo parseMsg(String msg, Calendar receivedDate) {
        msg = msg.replace("להסרה: השב הסר.", "");

        String[] words = msg.split(" ");

        StringBuilder location = new StringBuilder();
        for (int i = 13; i < words.length - 1; i++) {
            location.append(words[i]);
            location.append(" ");
        }
        location.deleteCharAt(0);

        String[] date = words[8].split("/");
        if (date.length < 2) {
            return null;
        }

        String title = "כללית - " + words[5].substring(1)  + " " + words[6];
        int day = 0;
        int month = 0;
        int hour = 0;
        int minute = 0;

        String[] time = words[12].replace(",","").split(":");

        try {
            day = Integer.parseInt(date[0]);
            month = Integer.parseInt(date[1]);
            hour = Integer.parseInt(time[0]);
            minute = Integer.parseInt(time[1]);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Caught NumberFormatException: " + e.getMessage());
        }

        return  new EventInfo(month, day, hour, minute, location.toString(), title,
                "Doctor Visit", receivedDate);

    }
}
