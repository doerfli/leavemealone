package li.doerf.leavemealone.ui.fragments;


import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import li.doerf.leavemealone.R;
import li.doerf.leavemealone.util.NotificationHelper;

/**
 * Created by moo on 01/12/15.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private final String LOGTAG = getClass().getSimpleName();
    private Integer mNotificationId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(LOGTAG, "preference changed: " + key);

        if ( getString(R.string.pref_key_only_allow_contacts).equals( key)) {
            boolean isSet = sharedPreferences.getBoolean(
                    getActivity().getBaseContext().getString(R.string.pref_key_only_allow_contacts), false);
            Log.i( LOGTAG, "only allow contacts: " + isSet);
            NotificationManager mNotifyMgr =
                    (NotificationManager) getActivity().getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);

            if ( isSet) {
                android.support.v4.app.NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder( getActivity().getBaseContext())
                                .setSmallIcon(R.drawable.ic_contact_phone_white_48dp)
                                .setContentTitle("Only calls from contacts allowed");
                Notification notification = mBuilder.build();
                notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;

                // Sets an ID for the notification
                // Gets an instance of the NotificationManager service
                // Builds the notification and issues it.
                mNotificationId = NotificationHelper.getNotificationId();
                mNotifyMgr.notify(mNotificationId, notification);
                Log.d(LOGTAG, "notification build and issued");
            } else {
                if ( mNotificationId != null ) {
                    mNotifyMgr.cancel(mNotificationId);
                    mNotificationId = null;
                    Log.d( LOGTAG, "removed notification");
                }
            }
        }
    }
}
