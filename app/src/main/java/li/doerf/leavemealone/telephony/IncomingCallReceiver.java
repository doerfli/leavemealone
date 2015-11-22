package li.doerf.leavemealone.telephony;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

import li.doerf.leavemealone.LeaveMeAloneApplication;
import li.doerf.leavemealone.db.AloneSQLiteHelper;
import li.doerf.leavemealone.db.tables.PhoneNumber;
import li.doerf.leavemealone.util.PhoneNumberHelper;

/**
 * Created by moo on 15/11/15.
 */
public class IncomingCallReceiver extends BroadcastReceiver {
    private final String LOGTAG = getClass().getSimpleName();

    /**
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String state = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
        String incomingNumber = PhoneNumberHelper.normalize(intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER));
        Log.d(LOGTAG, "reveived state change. state: "
                + state
                + ". number: "
                + incomingNumber);

        if ( incomingNumber == null ) {
            // nothing to see here
            return;
        }

        if ( ! TelephonyManager.EXTRA_STATE_RINGING.equals( state) ) {
            // we are not interested if its not ringing
            return;
        }

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

        SQLiteDatabase readableDb = AloneSQLiteHelper.getInstance(context).getReadableDatabase();
        PhoneNumber number = PhoneNumber.findByNumber(readableDb, incomingNumber);

        if (number != null) {
            hangupCall(context, incomingNumber);
        } else {
            Log.d(LOGTAG, "no matching number found");
        }

        readableDb.close();
    }

    /**
     * Check state the master switch in the preferences.
     * @param aContext
     * @return <code>true</code> if the master switch is enabled and number should be blocked, <code>false</code> otherwise.
     */
    public boolean isMasterSwitchEnabled( Context aContext) {
        SharedPreferences settings = aContext.getSharedPreferences(LeaveMeAloneApplication.PREFS_FILE, 0);
        return settings.getBoolean( LeaveMeAloneApplication.PREF_BLOCKER_ON_OFF, false);
    }

    /**
     * Move to separate class with interface
     * @param context
     * @param incomingNumber
     */
    private void hangupCall(Context context, String incomingNumber) {
        // hang up call
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Class c = Class.forName(tm.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            ITelephony telephonyService = (ITelephony) m.invoke(tm);
            telephonyService.endCall();
            Log.i(LOGTAG, "HANG UP " + incomingNumber);
            // TODO show notification of blocked call
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
