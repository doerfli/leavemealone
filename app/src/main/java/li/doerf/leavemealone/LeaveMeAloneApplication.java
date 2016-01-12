package li.doerf.leavemealone;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import li.doerf.leavemealone.db.AloneSQLiteHelper;
import li.doerf.leavemealone.db.tables.PhoneNumber;
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
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        AloneSQLiteHelper.getInstance(getApplicationContext()).close();
    }
}
