package apps.rachamimr.smstoevent;

import android.util.Log;

import java.util.Calendar;

public class Macabi implements MsgParser {
    private static final String TAG = "MacabiParser";
    /* return if the message should be parsed by this parser */
    public boolean shouldHandleMsg(String msg) {
        return msg.contains("מכבידנט");
    }

    /* Parses the message */
    public EventInfo parseMsg(String msg, Calendar receivedDate) {
        String MSG_PREFIX = "נקבע תור";

        String[] words = msg.substring(msg.indexOf(MSG_PREFIX)).split(" ");

        if (words.length < 7) {
            return null;
        }

        String[] date = words[5].split("/");
        if (date.length < 2) {
            return null;
        }

        String title = "מכבי דנט";
        String location = "מכבי דנט ראשלצ גן העיר";
        int day = 0;
        int month = 0;
        int hour = 0;
        int minute = 0;

        String[] time = words[6].split(":");

        try {
            day = Integer.parseInt(date[0]);
            month = Integer.parseInt(date[1]);
            hour = Integer.parseInt(time[0].substring(1));
            minute = Integer.parseInt(time[1]);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Caught NumberFormatException: " + e.getMessage());
        }
        return  new EventInfo(month, day, hour, minute, location, title,
                "Doctor Visit", receivedDate);

    }
}
