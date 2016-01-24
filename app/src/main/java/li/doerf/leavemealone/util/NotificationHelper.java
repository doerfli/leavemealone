package li.doerf.leavemealone.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.util.concurrent.atomic.AtomicInteger;

import li.doerf.leavemealone.R;
import li.doerf.leavemealone.activities.SettingsActivity;

/**
 * Created by moo on 04/12/15.
 */
public class NotificationHelper {
    private final static String LOGTAG = "NotificationHelper";
    private final static AtomicInteger notifyId = new AtomicInteger();
    public static final String PREF_KEY_NOTIFICATION_ID_ONLY_FROM_CONTACTS = "OnlyAllowFromContactsNotificationId";

    public static int getNotificationId() {
        return notifyId.incrementAndGet();
    }

    public static int notify(Context aContext, Notification aNotification) {
        // Sets an ID for the notification
        int notificationId = NotificationHelper.getNotificationId();
        // Gets an instance of the NotificationManager service
        NotificationManager notificationManager =
                (NotificationManager) aContext.getSystemService(Context.NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        notificationManager.notify(notificationId, aNotification);
        Log.d(LOGTAG, "notification build and issued: " + notificationId);
        return notificationId;
    }

    /**
     * set/unset the notification to indicate that only calls from contacts are allowed.
     * This is done by evaluation the state of the preference {@link li.doerf.leavemealone.R.string#pref_key_master_switch}
     * and {@link li.doerf.leavemealone.R.string#pref_key_only_allow_contacts}.
     * If both are set, then check if the notification is active (id > -1). If not, created a new
     * notification. If the keys are not set, then remove any existing notification (if it is set).
     *
     * @param aContext the application context
     */
    public static void setNotificationOnlyFromContacts(Context aContext) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(aContext);

        boolean isMasterSwitchSet = settings.getBoolean(
                aContext.getString(R.string.pref_key_master_switch), false);
        boolean isAlwaysAllowContacts = settings.getBoolean(
                aContext.getString(R.string.pref_key_always_allow_contacts), false);
        boolean isOnlyAllowContacts = settings.getBoolean(
                aContext.getString(R.string.pref_key_only_allow_contacts), false);
        Log.d(LOGTAG, "isMasterSwitchSet: " + isMasterSwitchSet);
        Log.d(LOGTAG, "isAlwaysAllowContacts: " + isAlwaysAllowContacts);
        Log.d(LOGTAG, "isOnlyAllowContacts: " + isOnlyAllowContacts);

        int notificationId = settings.getInt(PREF_KEY_NOTIFICATION_ID_ONLY_FROM_CONTACTS, -1);
        NotificationManager notificationManager =
                (NotificationManager) aContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (isMasterSwitchSet && isAlwaysAllowContacts && isOnlyAllowContacts) {
            if (notificationId > -1) {
                return;
            }

            android.support.v4.app.NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(aContext)
                            .setSmallIcon(R.drawable.ic_contact_phone_white_48dp)
                            .setContentTitle(aContext.getString(R.string.only_contacts_allowed_notification_title))
                            .setContentText(aContext.getString(R.string.only_contacts_allowed_notification_subtitle));
            PendingIntent settingsPendingIntent =
                    PendingIntent.getActivity(
                            aContext,
                            0,
                            new Intent(aContext, SettingsActivity.class),
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(settingsPendingIntent);
            Notification notification = mBuilder.build();

            notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
            notificationId = NotificationHelper.notify(aContext, notification);
            Log.d(LOGTAG, "issued notification: " + notificationId);
            settings.edit().putInt(PREF_KEY_NOTIFICATION_ID_ONLY_FROM_CONTACTS, notificationId).commit();
        } else {
            if (notificationId > -1) {
                notificationManager.cancel(notificationId);
                Log.d(LOGTAG, "cancelled notification: " + notificationId);
                settings.edit().putInt(PREF_KEY_NOTIFICATION_ID_ONLY_FROM_CONTACTS, -1).commit();
            }
        }
    }

    public static void resetNotificationOnlyFromContacts(Context aContext) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(aContext);
        int notificationId = settings.getInt(PREF_KEY_NOTIFICATION_ID_ONLY_FROM_CONTACTS, -1);
        if (notificationId > -1) {
            NotificationManager notificationManager =
                    (NotificationManager) aContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(notificationId);
            settings.edit().putInt(NotificationHelper.PREF_KEY_NOTIFICATION_ID_ONLY_FROM_CONTACTS, -1).commit();
        }
        setNotificationOnlyFromContacts(aContext);
    }

    public static int showSyncingNotification(Context aContext) {
        android.support.v4.app.NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(aContext)
                        .setSmallIcon(R.drawable.ic_sync_white_48dp)
                        .setContentTitle(aContext.getString(R.string.notificaion_syncing_blocklist_title));
        Notification notification = mBuilder.build();

        notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        int notificationId = NotificationHelper.notify(aContext, notification);
        Log.d(LOGTAG, "issued notification: " + notificationId);
        return notificationId;
    }

    public static void hideSyncingNotification(Context aContext, int aNotificationId) {
        NotificationManager notificationManager =
                (NotificationManager) aContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(aNotificationId);
    }
}
