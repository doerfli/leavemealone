package li.doerf.leavemealone.telephony;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v7.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.concurrent.atomic.AtomicInteger;

import li.doerf.leavemealone.R;
import li.doerf.leavemealone.db.AloneSQLiteHelper;
import li.doerf.leavemealone.db.tables.PhoneNumber;
import li.doerf.leavemealone.util.PhoneNumberHelper;

/**
 * Created by moo on 15/11/15.
 */
public class IncomingCallReceiver extends BroadcastReceiver {
    private final String LOGTAG = getClass().getSimpleName();
    private final static AtomicInteger notifyId = new AtomicInteger();
    private static String myIsRingingFrom = null;

    /**
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String state = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
        String incomingNumber = PhoneNumberHelper.normalize(intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER));

        if ( myIsRingingFrom != null && myIsRingingFrom.equals( incomingNumber)) {
            // already ringing
            return;
        }

        Log.d(LOGTAG, "reveived state change. state: "
                + state
                + ". number: "
                + incomingNumber);

        if ( incomingNumber == null ) {
            myIsRingingFrom = null;
            // nothing to see here
            return;
        }

        if ( ! TelephonyManager.EXTRA_STATE_RINGING.equals( state) ) {
            myIsRingingFrom = null;
            // we are not interested if its not ringing
            return;
        }

        myIsRingingFrom = incomingNumber;

        checkNumber(context, incomingNumber);
    }

    /**
     * Check of the number is registered in the database and if so, block it.
     *
     * @param context the context
     * @param incomingNumber the number to check
     */
    private void checkNumber(Context context, String incomingNumber) {
        if ( ! isMasterSwitchEnabled( context) ) {
            return;
        }

        if ( isNumberInContacts( context, incomingNumber)) {
            return;
        }

        SQLiteDatabase readableDb = AloneSQLiteHelper.getInstance(context).getReadableDatabase();
        PhoneNumber number = PhoneNumber.findByNumber(readableDb, incomingNumber);

        if (number != null) {
            hangupCall(context, incomingNumber, number.getName());
        } else {
            Log.d(LOGTAG, "no matching number found");
        }
    }

    /**
     * Check state the master switch in the preferences.
     * @param aContext
     * @return <code>true</code> if the master switch is enabled and number should be blocked, <code>false</code> otherwise.
     */
    public boolean isMasterSwitchEnabled( Context aContext) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(aContext);
        return settings.getBoolean( aContext.getString(R.string.pref_key_master_switch), false);
    }

    public boolean isNumberInContacts( Context aContext, String number) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

        ContentResolver contentResolver = aContext.getContentResolver();
        Cursor contactLookup = contentResolver.query(uri, new String[]{BaseColumns._ID,
                ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);

        try {
            if (contactLookup != null && contactLookup.getCount() > 0) {
                contactLookup.moveToNext();
                String name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                Log.d(LOGTAG, "found contact for number: " + name);
                return true;
            }
        } finally {
            if (contactLookup != null) {
                contactLookup.close();
            }
        }

        return false;
    }

    /**
     * Hang up the call.
     * @param context
     * @param incomingNumber
     * @param aName
     */
    private void hangupCall(Context context, String incomingNumber, String aName) {
        ICallHangup ch = new CallHangupFactory().get();
        boolean s = ch.hangup( context);
        Log.i(LOGTAG, "HANG UP " + incomingNumber + " successful: " + s);
        if ( s) {
            showNotificationCallBlocked( context, incomingNumber, aName);
        }
    }

    private void showNotificationCallBlocked(Context aContext, String incomingNumber, String aName) {
        android.support.v4.app.NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder( aContext)
                        // TODO show icon
//                        .setSmallIcon(android.support.v7.appcompat.R.drawable.)
                        .setSmallIcon(R.drawable.ic_not_interested_white_24dp)
                        .setContentTitle("Call blocked")
                        .setContentText(incomingNumber + " (" + aName + ")");

        Intent showCallLog = new Intent();
        showCallLog.setAction(Intent.ACTION_VIEW);
        showCallLog.setType(CallLog.Calls.CONTENT_TYPE);
//        context.startActivity(showCallLog);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        aContext,
                        0,
                        showCallLog,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        // Sets an ID for the notification
        int mNotificationId = notifyId.incrementAndGet();
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) aContext.getSystemService(Context.NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
        Log.d(LOGTAG, "notification build and issued");
    }
}
