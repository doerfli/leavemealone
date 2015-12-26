package li.doerf.leavemealone.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import li.doerf.leavemealone.util.NotificationHelper;
import li.doerf.leavemealone.util.SynchronizationHelper;

/**
 * Checks and sets notification of reboot if required.
 *
 * Created by moo on 04/12/15.
 */
public class BootupCompleteReceiver extends BroadcastReceiver {
    private final String LOGTAG = getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOGTAG, "check if notification needs to be set after reboot");
        NotificationHelper.resetNotificationOnlyFromContacts(context);
        SynchronizationHelper.scheduleSync(context);
    }
}
