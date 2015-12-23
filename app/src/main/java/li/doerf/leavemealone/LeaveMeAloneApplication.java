package li.doerf.leavemealone;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import li.doerf.leavemealone.db.AloneSQLiteHelper;
import li.doerf.leavemealone.services.KtippBlocklistRetrievalService;
import li.doerf.leavemealone.util.NotificationHelper;

/**
 * The application
 * Created by moo on 17/11/15.
 */
public class LeaveMeAloneApplication extends Application {
    private final String LOGTAG = getClass().getSimpleName();

    @Override
    public void onCreate() {
        Log.v(LOGTAG, "Application onCreate");
        super.onCreate();
        NotificationHelper.resetNotificationOnlyFromContacts(getBaseContext());

        // TODO start this only when required (settings) and with configurable intervals.
        // TODO make sure its restarted after bootup
        Intent i = new Intent( getApplicationContext(), KtippBlocklistRetrievalService.class);
        PendingIntent ktipsync = PendingIntent.getService( getApplicationContext(), 0, i, 0);

        // TODO set correct intervals
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                0,
                24 * 60 * 60 * 1000, // once a day
                ktipsync);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        AloneSQLiteHelper.getInstance(getApplicationContext()).close();
    }
}
