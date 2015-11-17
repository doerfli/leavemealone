package li.doerf.leavemealone;

import android.app.Application;
import android.util.Log;

import li.doerf.leavemealone.db.AloneSQLiteHelper;

/**
 * Created by moo on 17/11/15.
 */
public class LeaveMeAloneApplication extends Application {
    private final String LOGTAG = getClass().getSimpleName();
    private AloneSQLiteHelper myDb;

    @Override
    public void onCreate() {
        Log.v(LOGTAG, "Application onCreate");
        super.onCreate();
        myDb = new AloneSQLiteHelper( getApplicationContext());
    }

    @Override
    public void onTerminate() {
        myDb.close();
        super.onTerminate();
    }

    public AloneSQLiteHelper getDbHelper() {
        return myDb;
    }
}
