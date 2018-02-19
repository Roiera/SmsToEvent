package apps.rachamimr.smstoevent;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.support.design.widget.Snackbar;
import android.provider.CalendarContract;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.Queue;

public class MainActivity extends AppCompatActivity
    implements ActivityCompat.OnRequestPermissionsResultCallback {
    private Queue<EventInfo> eventsInfo;
    private MainApp mainApp;

    private static final String TAG = "MainActivity";

    private static final int REQUEST_APP_PERMISSIONS = 0;
    private static final String[] PERMISSIONS_APP = { Manifest.permission.READ_CALENDAR,
        Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_SMS };

    private int requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_APP, REQUEST_APP_PERMISSIONS );
            return -1;
        }

        return 0;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_APP_PERMISSIONS) {
            Log.i(TAG, "Received response for application permissions request.");

            // We have requested multiple permissions for contacts, so all of them need to be
            // checked.
            if (PermissionUtil.verifyPermissions(grantResults)) {
                processMessages();
            } else {
                Log.i(TAG, "Application permissions were NOT granted.");
                Snackbar.make(findViewById(R.id.MainLayout),
                        "Application permissions not granted", Snackbar.LENGTH_LONG).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private String getPrimaryCalendarID() {
        String projection[] = {CalendarContract.Events._ID};
        ContentResolver cr = getContentResolver();
        Cursor cursor;
        String calID = "0";

        cursor = cr.query(CalendarContract.Calendars.CONTENT_URI, projection,
                CalendarContract.Events.IS_PRIMARY + "=1", null, null);

        if (cursor.moveToFirst()){
            int idCol = cursor.getColumnIndex(projection[0]);
            do {
                calID = cursor.getString(idCol);
            } while(cursor.moveToNext());
            cursor.close();
        }

        return calID;
    }

    private int createEvent(EventInfo eventInfo) {
        ContentResolver cr = getContentResolver();
        Calendar beginTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();
        beginTime.set(eventInfo.year, eventInfo.month - 1, eventInfo.day, eventInfo.hour, eventInfo.minute);
        endTime.set(eventInfo.year, eventInfo.month - 1, eventInfo.day, eventInfo.hour + 1, eventInfo.minute);

        String[] projection = new String[]{
                        CalendarContract.Instances._ID,
                        CalendarContract.Instances.BEGIN,
                        CalendarContract.Instances.END,
                        CalendarContract.Instances.EVENT_ID};
        Cursor cursor =
                CalendarContract.Instances.query(getContentResolver(), projection,
                        beginTime.getTimeInMillis(), endTime.getTimeInMillis(),
                        "\"" + eventInfo.description + "\"");
        if (cursor.getCount() > 0) {
            return -1;
        }

        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, beginTime.getTimeInMillis());
        values.put(CalendarContract.Events.DTEND, endTime.getTimeInMillis());
        values.put(CalendarContract.Events.TITLE, eventInfo.title);
        values.put(CalendarContract.Events.DESCRIPTION, eventInfo.description);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Jerusalem");
        values.put(CalendarContract.Events.CALENDAR_ID, getPrimaryCalendarID());

        cr.insert(CalendarContract.Events.CONTENT_URI, values);

        return 0;
    }

    private void processMessages() {
        String[] projection = new String[] {
                Telephony.TextBasedSmsColumns.ADDRESS,
                Telephony.TextBasedSmsColumns.BODY,
                Telephony.TextBasedSmsColumns.DATE
        };
        String WHERE_CONDITION = "date >= ?";
        Calendar startDate = Calendar.getInstance();
        startDate.set(2018,1,1);

        Cursor cur = getContentResolver().query(
                Telephony.Sms.Inbox.CONTENT_URI,
                projection,
                WHERE_CONDITION,
                new String[] { Long.toString(startDate.getTimeInMillis()) },
                Telephony.TextBasedSmsColumns.DATE
        );

        while (cur != null && cur.moveToNext()) {
            String address = cur.getString(cur.getColumnIndex(Telephony.TextBasedSmsColumns.ADDRESS));
            String body = cur.getString(cur.getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.BODY));
            Calendar receivedDate = Calendar.getInstance();
            receivedDate.setTimeInMillis(cur.getLong(cur.getColumnIndexOrThrow(Telephony.TextBasedSmsColumns.DATE)));

            EventInfo eventInfo = mainApp.processMsg(address, body, receivedDate);

            if (eventInfo != null) {
                eventsInfo.add(eventInfo);
            }
        }

        if (cur != null) {
            cur.close();
        }

        showEventDialog();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mainApp = new MainApp();
        eventsInfo = new LinkedList<>();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button button = findViewById(R.id.createEventsButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (requestPermissions() < 0) {
                    return;
                }

                processMessages();
            }
        });
    }

    private void showEventDialog() {
        if (!eventsInfo.isEmpty()) {
            Builder builder = new Builder((MainActivity.this));
            builder.setMessage(eventsInfo.peek().toString()).setPositiveButton("Add Event", dialogClickListener).
                    setNegativeButton("Discard", dialogClickListener).show();
        }
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        int res = createEvent(eventsInfo.peek());
                        String message;

                        if (res < 0) {
                            message = "Event already exists";
                        } else {
                            message = "Event created";
                        }

                        Snackbar.make(findViewById(R.id.MainLayout),
                                message, Snackbar.LENGTH_LONG).show();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
            }

            eventsInfo.poll();
            showEventDialog();
        }
    };
}
