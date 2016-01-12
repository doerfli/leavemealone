package li.doerf.leavemealone.ui.fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;

import li.doerf.leavemealone.R;
import li.doerf.leavemealone.util.NotificationHelper;
import li.doerf.leavemealone.util.SynchronizationHelper;

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

        if ( getString(R.string.pref_key_master_switch).equals( key) ||
                getString(R.string.pref_key_always_allow_contacts).equals( key) ||
                getString(R.string.pref_key_only_allow_contacts).equals( key)
                ) {
            NotificationHelper.setNotificationOnlyFromContacts( getActivity().getBaseContext());
        }

        if ( getString(R.string.pref_key_sync_enable).equals( key) ||
                getString( R.string.pref_key_sync_interval).equals( key)
                ) {
            SynchronizationHelper.scheduleSync( getActivity().getApplicationContext());
        }
    }
}
