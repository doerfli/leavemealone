package li.doerf.leavemealone;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import li.doerf.leavemealone.db.AloneSQLiteHelper;

/**
 * The application
 * Created by moo on 17/11/15.
 */
public class LeaveMeAloneApplication extends Application {
    private final String LOGTAG = getClass().getSimpleName();
    public static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 42;
    public static final int PERMISSIONS_REQUEST_READ_CONTACTS = 43;

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

    public String getAppVersion() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(LOGTAG, "exception while retrieving version", e);
        }
        return "Unknown";
    }
}
