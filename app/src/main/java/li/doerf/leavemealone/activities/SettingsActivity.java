package li.doerf.leavemealone.activities;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import li.doerf.leavemealone.LeaveMeAloneApplication;
import li.doerf.leavemealone.R;
import li.doerf.leavemealone.ui.fragments.SettingsFragment;

/**
 * Created by moo on 01/12/15.
 */
public class SettingsActivity extends AppCompatActivity {
    private final String LOGTAG = getClass().getSimpleName();
    private SettingsFragment mySettingsFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mySettingsFragment = new SettingsFragment();
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, mySettingsFragment)
                .commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case LeaveMeAloneApplication.PERMISSIONS_REQUEST_READ_PHONE_STATE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(LOGTAG, "permission READ_PHONE_STATE granted by user");
                    mySettingsFragment.handleSettingChanged(getString(R.string.pref_key_master_switch));
                } else {
                    Log.i(LOGTAG, "permission READ_PHONE_STATE denied by user");
                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean(getString(R.string.pref_key_master_switch), false);
                    editor.apply();
                    mySettingsFragment.refresh();
                }
                break;
            }
            case LeaveMeAloneApplication.PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(LOGTAG, "permission READ_CONTACTS granted by user");
                    mySettingsFragment.handleSettingChanged(getString(R.string.pref_key_always_allow_contacts));
                } else {
                    Log.i(LOGTAG, "permission READ_CONTACTS denied by user");
                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean(getString(R.string.pref_key_always_allow_contacts), false);
                    editor.apply();
                    mySettingsFragment.refresh();
                }
                break;
            }
        }
    }
}
