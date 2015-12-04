package li.doerf.leavemealone.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import li.doerf.leavemealone.util.NotificationHelper;

/**
 * Checks and sets notification of reboot if required.
 *
 * Created by moo on 04/12/15.
 */
public class CheckForNotificationAtBootReceiver extends BroadcastReceiver {
    private final String LOGTAG = getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOGTAG, "check if notification needs to be set after reboot");
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        // reset notificationid as notifications as not perisisted across reboot
        settings.edit().putInt( NotificationHelper.PREF_KEY_NOTIFICATION_ID_ONLY_FROM_CONTACTS, -1).commit();
        NotificationHelper.setNotificationOnlyFromContacts( context);
    }
}
