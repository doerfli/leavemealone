package li.doerf.leavemealone.util;

import android.util.Log;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

/**
 * Created by moo on 18/11/15.
 */
public class PhoneNumberHelper {
    private static final String LOGTAG = "PhoneNumberHelper";

    /**
     * normalize number to E164 format. Returns null if null is provided.
     *
     * @param aNumber
     * @return
     */
    public static String normalize( String aNumber) {
        if ( aNumber == null ) { return null; }

        PhoneNumberUtil pnu = PhoneNumberUtil.getInstance();
        try {
            String numberE164 = pnu.format(pnu.parse(aNumber, "CH"), PhoneNumberUtil.PhoneNumberFormat.E164);
            Log.d(LOGTAG, numberE164);
            return numberE164;
        } catch (NumberParseException e) {
            Log.e(LOGTAG, "caught NumberParseException", e);
            return aNumber;
        }
    }

    public static boolean isValid( String aNumber) {
        if ( aNumber == null ) { return false; }

        PhoneNumberUtil pnu = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber swissNumberProto = null;
        try {
            swissNumberProto = pnu.parse(aNumber, "CH");
            return pnu.isValidNumber( swissNumberProto);
        } catch (NumberParseException e) {
//            Log.w( LOGTAG, "caught NumberParseException", e);
            return false;
        }
    }
}
