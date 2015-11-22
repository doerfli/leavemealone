package li.doerf.leavemealone;

import android.app.Application;
import android.util.Log;

/**
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
    }
}
