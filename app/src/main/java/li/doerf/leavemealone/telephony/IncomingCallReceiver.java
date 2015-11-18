package li.doerf.leavemealone.telephony;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

import li.doerf.leavemealone.util.PhoneNumberHelper;

/**
 * Created by moo on 15/11/15.
 */
public class IncomingCallReceiver extends BroadcastReceiver {
    private final String LOGTAG = getClass().getSimpleName();

    /**
     * TODO this might be called twice. make sure that is handeled
     *
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


        if ( TelephonyManager.EXTRA_STATE_RINGING.equals( state) ) {
            // hang up call
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            try {
                Class c = Class.forName(tm.getClass().getName());
                Method m = c.getDeclaredMethod("getITelephony");
                m.setAccessible(true);
                ITelephony telephonyService = (ITelephony) m.invoke(tm);
                telephonyService.endCall();
                Log.i(LOGTAG, "HANG UP " + incomingNumber);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
