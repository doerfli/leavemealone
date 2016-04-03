package li.doerf.leavemealone.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import li.doerf.leavemealone.R;
import li.doerf.leavemealone.services.KtippBlocklistRetrievalService;

/**
 * Created by moo on 26/12/15.
 */
public class SynchronizationHelper {

    private static final String LOGTAG = "SynchronizationHelper";

    public static void scheduleSync(Context aContext) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(aContext);
        disableSync(aContext);
        if (settings.getBoolean(aContext.getString(R.string.pref_key_sync_enable), false)) {
            String intervalDesc = settings.getString(aContext.getString(R.string.pref_key_sync_interval), "onceaday") ;
            enableSync(aContext, translateIntervalDesc(intervalDesc));
        }
    }

    private static long translateIntervalDesc(String intervalDesc) {
        switch(intervalDesc) {
            case "onceaminute":
                return 1000 * 60;
            case "onceahour":
                return AlarmManager.INTERVAL_HOUR;
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

    private static void enableSync(Context aContext, long aInterval) {
        PendingIntent ktipsync = getPendingIntent(aContext);

        AlarmManager alarmManager = (AlarmManager) aContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                0,
                aInterval,
                ktipsync);
        Log.i(LOGTAG, "scheduled service to run every " + aInterval + " ms");
    }

    private static PendingIntent getPendingIntent(Context aContext) {
        Intent i = new Intent(aContext, KtippBlocklistRetrievalService.class);
        return PendingIntent.getService(aContext, 0, i, 0);
    }

    private static void disableSync(Context aContext) {
        AlarmManager alarmManager = (AlarmManager) aContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(getPendingIntent(aContext));
        Log.i(LOGTAG, "cancelled pending intent");
    }
}
