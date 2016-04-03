package li.doerf.leavemealone.telephony;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

/**
 * Implementation of {@link ICallHangup} that uses the {@link ITelephony} interface (the only way
 * to hang up a call when API < 23).
 */
public class TelephonyServiceCallHangup implements ICallHangup {
    private final String LOGTAG = getClass().getSimpleName();

    public boolean hangup(Context aContext) {
        int permissionCheck = ContextCompat.checkSelfPermission(aContext,
                Manifest.permission.CALL_PHONE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.e(LOGTAG, "Missing permission MODIFY_PHONE_STATE, cannot hangup call");
            return false;
        }

        TelephonyManager tm = (TelephonyManager) aContext.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Class c = Class.forName(tm.getClass().getName());
            @SuppressWarnings("unchecked") Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            ITelephony telephonyService = (ITelephony) m.invoke(tm);
            telephonyService.endCall();
            Log.d(LOGTAG, "hang up successful");
            return true;
        } catch (Exception e) {
            Log.e(LOGTAG, "call hang up failed", e);
            return false;
        }
    }
}
