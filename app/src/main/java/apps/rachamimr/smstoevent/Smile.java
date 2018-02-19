package apps.rachamimr.smstoevent;

import java.util.Calendar;

public class Smile implements MsgParser {
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
        int day = Integer.parseInt(date[0]);
        int month = Integer.parseInt(date[1]);

        String[] time = dateStr[8].split(":");

        int hour = Integer.parseInt(time[0]);
        int minute = Integer.parseInt(time[1].substring(0, 1));

        return  new EventInfo(month, day, hour, minute, location.toString(), title,
                "Doctor Visit", receivedDate);
    }
}
