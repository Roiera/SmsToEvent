package apps.rachamimr.smstoevent;

import java.util.ArrayList;
import java.util.Calendar;

public class MainApp {
    private ArrayList<MsgParser> msgParsers;

    private ArrayList<MsgParser> createParsers() {
        ArrayList<MsgParser> msgParsers = new ArrayList<>();

        msgParsers.add(new Smile());
        msgParsers.add(new Clalit());
        msgParsers.add(new Macabi());

        return msgParsers;
    }

    public MainApp() {
        msgParsers = createParsers();
    }

    public EventInfo processMsg(String address, String body, Calendar receivedDate) {
        EventInfo eventInfo = null;

        for (MsgParser msgParser : msgParsers) {
            if (msgParser.shouldHandleMsg(body)) {
                eventInfo = msgParser.parseMsg(body, receivedDate);
            }
        }

        return eventInfo;
    }
}