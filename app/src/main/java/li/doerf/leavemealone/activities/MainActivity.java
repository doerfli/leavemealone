package li.doerf.leavemealone.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import li.doerf.leavemealone.R;
import li.doerf.leavemealone.db.tables.PhoneNumber;
import li.doerf.leavemealone.ui.dialogs.AddNumberDialogFragment;
import li.doerf.leavemealone.ui.fragments.BockedNumbersListFragment;
import li.doerf.leavemealone.util.NotificationHelper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AddNumberDialogFragment.NumberAddedListener {

    private static final String LOGTAG = "MainActivity";
    private BockedNumbersListFragment myBockedNumbersFragment;
    private Switch myMasterSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myBockedNumbersFragment = BockedNumbersListFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add( R.id.fragment_container, myBockedNumbersFragment).commit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddNumberDialogFragment newFragment = new AddNumberDialogFragment();
                newFragment.show(getSupportFragmentManager(), "addnumber");
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if ( myMasterSwitch != null ) {
            boolean masterSwitchEnabled = isMasterSwitchEnabled();
            Log.d(LOGTAG, "setting master switch: " + masterSwitchEnabled);
            myMasterSwitch.setChecked(masterSwitchEnabled);
        }
    }

    @Override
    public void onBackPressed() {
        myBockedNumbersFragment.backButtonPressed();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        myMasterSwitch = (Switch) menu.findItem(R.id.action_switch).getActionView().findViewById(R.id.master_switch);

        boolean masterSwitchEnabled = isMasterSwitchEnabled();
        Log.d(LOGTAG, "setting master switch: " + masterSwitchEnabled);
        myMasterSwitch.setChecked(masterSwitchEnabled);

        myMasterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleMasterSwitch(isChecked);
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent( getBaseContext(), SettingsActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void toggleMasterSwitch(boolean isChecked) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(getString(R.string.pref_key_master_switch), isChecked);
        editor.commit();
        Log.i(LOGTAG, "app master switch: " + isChecked);

        NotificationHelper.setNotificationOnlyFromContacts(getBaseContext());
        String snackText = isChecked ? getString( R.string.call_blocker_enabled) : getString(R.string.call_blocker_disabled);
        Snackbar.make( this.findViewById( android.R.id.content), snackText, Snackbar.LENGTH_LONG).show();
    }

    private boolean isMasterSwitchEnabled() {
        return PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(getString(R.string.pref_key_master_switch), false);
    }

    @Override
    public void numberAdded(PhoneNumber aNumber) {
        Log.d(LOGTAG, "refreshing list");
        myBockedNumbersFragment.itemsAdded();
    }
}
