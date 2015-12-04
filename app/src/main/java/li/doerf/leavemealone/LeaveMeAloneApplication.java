package li.doerf.leavemealone;

import android.app.Application;
import android.util.Log;

import li.doerf.leavemealone.db.AloneSQLiteHelper;
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
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        AloneSQLiteHelper.getInstance(getApplicationContext()).close();
    }
}
