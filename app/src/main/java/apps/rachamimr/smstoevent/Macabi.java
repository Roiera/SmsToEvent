package apps.rachamimr.smstoevent;

import java.util.Calendar;

public class Macabi implements MsgParser {
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
        int day = Integer.parseInt(date[0]);
        int month = Integer.parseInt(date[1]);

        String[] time = words[6].split(":");

        int hour = Integer.parseInt(time[0].substring(1));
        int minute = Integer.parseInt(time[1]);

        return  new EventInfo(month, day, hour, minute, location, title,
                "Doctor Visit", receivedDate);

    }
}
