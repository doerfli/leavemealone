package li.doerf.leavemealone.ui.fragments;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import li.doerf.leavemealone.R;
import li.doerf.leavemealone.db.AloneSQLiteHelper;
import li.doerf.leavemealone.ui.adapters.BlockedNumbersAdapter;

/**
 * Created by moo on 23/03/16.
 */
public class CallLogFragment extends Fragment {
    private final String LOGTAG = getClass().getSimpleName();
    private SQLiteDatabase myReadbableDb;

    public static CallLogFragment newInstance() {
        CallLogFragment f = new CallLogFragment();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        myReadbableDb = AloneSQLiteHelper.getInstance(getContext()).getReadableDatabase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.fragment_call_log, container, false);

        return view;
    }

}
