package li.doerf.leavemealone.ui.fragments;


import android.Manifest;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import li.doerf.leavemealone.LeaveMeAloneApplication;
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

        if ( getString(R.string.pref_key_master_switch).equals(key) ) {
            boolean isMasterSwitchSet = sharedPreferences.getBoolean(
                    getActivity().getString(R.string.pref_key_master_switch), false);

            if ( isMasterSwitchSet) {
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.READ_PHONE_STATE)
                        != PackageManager.PERMISSION_GRANTED) {
                    Log.v(LOGTAG, "permission READ_PHONE_STATE denied");
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.READ_PHONE_STATE)) {
                        Log.v(LOGTAG, "show permission rationale");
                        new AlertDialog.Builder(getActivity())
                                .setTitle(getString(R.string.dialog_permission_request_title))
                                .setMessage(getString(R.string.dialog_permission_request_READ_PHONE_STATE_EXPLANATION))
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        ActivityCompat.requestPermissions(getActivity(),
                                                new String[]{Manifest.permission.READ_PHONE_STATE},
                                                LeaveMeAloneApplication.PERMISSIONS_REQUEST_READ_PHONE_STATE);
                                    }
                                }).show();
                    } else {
                        Log.i(LOGTAG, "request permission READ_PHONE_STATE");
                        // No explanation needed, we can request the permission.
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.READ_PHONE_STATE},
                                LeaveMeAloneApplication.PERMISSIONS_REQUEST_READ_PHONE_STATE);
                    }
                } else {
                    Log.v(LOGTAG, "permission READ_PHONE_STATE granted");
                }
            }
        }

        if ( getString(R.string.pref_key_always_allow_contacts).equals( key) ) {
            boolean isAlwaysAllowContacts = sharedPreferences.getBoolean(
                    getString(R.string.pref_key_always_allow_contacts), false);
            if ( isAlwaysAllowContacts) {
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {
                    Log.v(LOGTAG, "permission READ_CONTACTS denied");
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.READ_CONTACTS)) {
                        Log.v(LOGTAG, "show permission rationale");
                        new AlertDialog.Builder(getActivity())
                                .setTitle(getString(R.string.dialog_permission_request_title))
                                .setMessage(getString(R.string.dialog_permission_request_READ_CONTACTS_EXPLANATION))
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        ActivityCompat.requestPermissions(getActivity(),
                                                new String[]{Manifest.permission.READ_CONTACTS},
                                                LeaveMeAloneApplication.PERMISSIONS_REQUEST_READ_CONTACTS);
                                    }
                                }).show();
                    } else {
                        Log.i(LOGTAG, "request permission READ_CONTACTS");
                        // No explanation needed, we can request the permission.
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.READ_CONTACTS},
                                LeaveMeAloneApplication.PERMISSIONS_REQUEST_READ_CONTACTS);
                    }
                } else {
                    Log.v(LOGTAG, "permission READ_CONTACTS granted");
                }
            }
        }

        handleSettingChanged(key);
    }

    public void handleSettingChanged(String key) {
        if ( getString(R.string.pref_key_master_switch).equals( key) ||
                getString(R.string.pref_key_always_allow_contacts).equals( key) ||
                getString(R.string.pref_key_only_allow_contacts).equals( key)
                ) {
            NotificationHelper.setNotificationOnlyFromContacts(getActivity().getBaseContext());
        }

        if ( getString(R.string.pref_key_sync_enable).equals( key) ||
                getString( R.string.pref_key_sync_interval).equals( key)
                ) {
            SynchronizationHelper.scheduleSync(getActivity().getApplicationContext());
        }
    }

    public void refresh() {
        setPreferenceScreen(null);
        addPreferencesFromResource(R.xml.preferences);
    }
}
