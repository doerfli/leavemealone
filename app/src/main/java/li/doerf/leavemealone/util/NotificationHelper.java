package li.doerf.leavemealone.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by moo on 04/12/15.
 */
public class NotificationHelper {
    private final static String LOGTAG = "NotificationHelper";
    private final static AtomicInteger notifyId = new AtomicInteger();

    public static int getNotificationId() {
        return notifyId.incrementAndGet();
    }

    public static int notify( Context aContext, Notification aNotification) {
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
}
