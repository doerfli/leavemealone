package li.doerf.leavemealone.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by moo on 04/12/15.
 */
public class NotificationHelper {
    private final String LOGTAG = getClass().getSimpleName();
    private final static AtomicInteger notifyId = new AtomicInteger();

    public static int getNotificationId() {
        return notifyId.incrementAndGet();
    }
}
