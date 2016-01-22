package li.doerf.leavemealone.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

import java.security.acl.LastOwnerException;

import li.doerf.leavemealone.R;
import li.doerf.leavemealone.services.KtippBlocklistRetrievalService;

/**
 * Created by moo on 26/12/15.
 */
public class SynchronizationHelper {
    private static final String LOGTAG = "SynchronizationHelper";

    private static boolean _isConnected = false;
    private static boolean _isWifi = false;

    public static void networkStateChanged(Context aContext, boolean isConnected, boolean isWifi) {
        _isConnected = isConnected;
        _isWifi = isWifi;
        scheduleSync(aContext, isConnected, isWifi);
    }

    public static void handleSettingsChanged(Context aContext) {
        scheduleSync(aContext, _isConnected, _isWifi);
    }

    private static void scheduleSync(Context aContext, boolean isConnected, boolean isWifi) {
        stopServices(aContext);

        if (!isConnected) {
            return;
        }

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(aContext);
        if (!settings.getBoolean(aContext.getString(R.string.pref_key_sync_enable), false)) {
            return;
        }

        if (settings.getBoolean(aContext.getString(R.string.pref_key_sync_wifi), false) && !isWifi) {
            return;
        }

        String intervalDesc = settings.getString(aContext.getString(R.string.pref_key_sync_interval), "onceaday") ;
        startServices(aContext, translateIntervalDesc(intervalDesc));
    }

    private static long translateIntervalDesc(String intervalDesc) {
        switch(intervalDesc) {
            case "twiceaday":
                return AlarmManager.INTERVAL_HALF_DAY;
            case "onceaday":
                return AlarmManager.INTERVAL_DAY;
            case "onceaweek":
                return AlarmManager.INTERVAL_DAY * 7;
            default:
                return AlarmManager.INTERVAL_DAY;
        }
    }

    private static void startServices(Context aContext, long aIntervalMillis) {
        Log.i(LOGTAG, "schedule services to run every " + aIntervalMillis + " ms");
        KtippBlocklistRetrievalService.startService(aContext, aIntervalMillis);
    }

    private static void stopServices(Context aContext) {
        Log.i(LOGTAG, "cancel services");
        KtippBlocklistRetrievalService.stopService(aContext);
    }
}
