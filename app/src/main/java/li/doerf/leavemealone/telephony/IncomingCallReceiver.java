package li.doerf.leavemealone.telephony;

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

import li.doerf.leavemealone.R;
import li.doerf.leavemealone.db.AloneSQLiteHelper;
import li.doerf.leavemealone.db.tables.PhoneNumber;
import li.doerf.leavemealone.util.NotificationHelper;
import li.doerf.leavemealone.util.PhoneNumberHelper;

/**
 * Created by moo on 15/11/15.
 */
public class IncomingCallReceiver extends BroadcastReceiver {
    private final String LOGTAG = getClass().getSimpleName();

    private static String myIsRingingFrom = null;

    /**
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String state = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
        String incomingNumber = PhoneNumberHelper.normalize(context, intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER));
//        Log.v(LOGTAG, "reveived state change. state: "
//                + state
//                + ". number: "
//                + incomingNumber);

        if (myIsRingingFrom != null && myIsRingingFrom.equals( incomingNumber)) {
            // already ringing
            return;
        }

        Log.d(LOGTAG, "reveived state change. state: "
                + state
                + ". number: "
                + incomingNumber);

        if (incomingNumber == null) {
            myIsRingingFrom = null;
            // nothing to see here
            return;
        }

        if (!TelephonyManager.EXTRA_STATE_RINGING.equals( state)) {
            myIsRingingFrom = null;
            // we are not interested if its not ringing
            return;
        }

        myIsRingingFrom = incomingNumber;

        try {
            checkNumber(context, incomingNumber);
        } finally {
            myIsRingingFrom = null;
        }
    }

    /**
     * Check of the number is registered in the database and if so, block it.
     *
     * @param context the context
     * @param incomingNumber the number to check
     */
    private void checkNumber(Context context, String incomingNumber) {
        if (!isMasterSwitchEnabled( context)) {
            Log.d( LOGTAG, "call blocked disabled");
            return;
        }

        if (isAlwaysAllowContacts(context) ) {
            if (isNumberInContacts( context, incomingNumber)) {
                Log.i( LOGTAG, "number in contacts");
                return;
            }

            if (isOnlyAllowContacts( context)) {
                Log.i( LOGTAG, "number not in contacts");
                hangupCall(context, incomingNumber, "Caller not in contats");
                return;
            }
        }

        SQLiteDatabase readableDb = AloneSQLiteHelper.getInstance(context).getReadableDatabase();
        PhoneNumber number = PhoneNumber.findByNumber(readableDb, incomingNumber);
        if (number != null) {
            Log.i( LOGTAG, "number in list of blocked numbers");
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

    /**
     * Check state of the "always allow contacts" flag in the preferences.
     * @param aContext
     * @return
     */
    public boolean isAlwaysAllowContacts( Context aContext) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(aContext);
        return settings.getBoolean( aContext.getString(R.string.pref_key_always_allow_contacts), false);
    }

    /**
     * Check state of the "only allow contacts" flag in the preferences.
     * @param aContext
     * @return <code>true</code> if the master switch is enabled and number should be blocked, <code>false</code> otherwise.
     */
    public boolean isOnlyAllowContacts( Context aContext) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(aContext);
        return settings.getBoolean( aContext.getString(R.string.pref_key_only_allow_contacts), false);
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
                        .setSmallIcon(R.drawable.ic_not_interested_white_24dp)
                        .setContentTitle("Call blocked")
                        .setContentText(incomingNumber + " (" + aName + ")");

        Intent showCallLog = new Intent();
        showCallLog.setAction(Intent.ACTION_VIEW);
        showCallLog.setType(CallLog.Calls.CONTENT_TYPE);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        aContext,
                        0,
                        showCallLog,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationHelper.notify(aContext, mBuilder.build());
    }
}
