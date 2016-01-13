package li.doerf.leavemealone.util;

import android.content.Context;
import android.util.Log;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.Locale;

/**
 * Created by moo on 18/11/15.
 */
public class PhoneNumberHelper {
    private static final String LOGTAG = "PhoneNumberHelper";

    /**
     * normalize number to E164 format. Returns null if null is provided.
     *
     * @param aNumber the phone number to normalize
     * @return e164 phone number string
     */
    public static String normalize(Context context, String aNumber) {
        if (aNumber == null) { return null; }

        PhoneNumberUtil pnu = PhoneNumberUtil.getInstance();
        Locale locale = context.getResources().getConfiguration().locale;
        String countryIso = getCurrentCountryIso(context, locale);
        try {
//            Log.d(LOGTAG, countryIso);
            String numberE164 = pnu.format(pnu.parse(aNumber, countryIso), PhoneNumberUtil.PhoneNumberFormat.E164);
//            Log.d(LOGTAG, numberE164);
            return numberE164;
        } catch (NumberParseException e) {
            Log.w(LOGTAG, "normalize caught NumberParseException: number=" + aNumber);
            return aNumber;
        }
    }

    public static boolean isValid(Context context, String aNumber) {
        if (aNumber == null) { return false; }

        PhoneNumberUtil pnu = PhoneNumberUtil.getInstance();
        Locale locale = context.getResources().getConfiguration().locale;
        String countryIso = getCurrentCountryIso(context, locale);
        Phonenumber.PhoneNumber swissNumberProto;
        try {
            swissNumberProto = pnu.parse(aNumber, countryIso);
            return pnu.isValidNumber(swissNumberProto);
        } catch (NumberParseException e) {
            Log.w(LOGTAG, "isValid caught NumberParseException: number=" + aNumber);
            return false;
        }
    }

    /**
     * Get the current country code
     *
     * @return the ISO 3166-1 two letters country code of current country.
     */
    private static String getCurrentCountryIso(Context context, Locale locale) {
        return "CH";
        /*
        final CountryDetector detector = (CountryDetector) context.getSystemService(
                Context.COUNTRY_DETECTOR);
        if (detector != null) {
            final Country country = detector.detectCountry();
            if (country != null) {
                countryIso = country.getCountryIso();
            }
        }
        */
    }
}
