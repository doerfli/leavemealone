package li.doerf.leavemealone.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by moo on 15/12/15.
 */
public class KtippBlocklistRetrievalService extends Service {
    private final String LOGTAG = getClass().getSimpleName();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d( LOGTAG, "onBind");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d( LOGTAG, "onStartCommand - startId: " + startId);
        stopSelf(startId);
        return START_STICKY;
    }
}
