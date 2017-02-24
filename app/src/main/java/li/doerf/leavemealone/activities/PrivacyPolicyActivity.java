package li.doerf.leavemealone.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import li.doerf.leavemealone.R;
import li.doerf.leavemealone.ui.fragments.AboutFragment;
import li.doerf.leavemealone.ui.fragments.PrivacyPolicyFragment;

public class PrivacyPolicyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new PrivacyPolicyFragment())
                .commit();

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle( getString(R.string.title_privacy_policy));
    }
}
