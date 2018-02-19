package apps.rachamimr.smstoevent;

import java.util.Calendar;

public interface MsgParser {
    /* return if the message should be parsed by this parser */
    boolean shouldHandleMsg(String msg);

    /* Parses the message */
    EventInfo parseMsg(String msg, Calendar receivedDate);
}