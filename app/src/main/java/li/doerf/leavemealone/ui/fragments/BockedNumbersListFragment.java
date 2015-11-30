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
import li.doerf.leavemealone.db.tables.PhoneNumber;
import li.doerf.leavemealone.ui.adapters.BlockedNumbersAdapter;

/**
 * This fragment show the list of blocked numbers (loaded from the database).
 *
 * Created by moo on 23/11/15.
 */
public class BockedNumbersListFragment extends Fragment {
    private final String LOGTAG = getClass().getSimpleName();
    private RecyclerView myBlockedNumbersList;
    private BlockedNumbersAdapter myBlockedNumbersAdapter;
    private SQLiteDatabase myReadbableDb;

    public static BockedNumbersListFragment newInstance() {
        return new BockedNumbersListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myReadbableDb = AloneSQLiteHelper.getInstance(getContext()).getReadableDatabase();
        myBlockedNumbersAdapter = new BlockedNumbersAdapter( getContext(), null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.fragment_blocked_numbers_list, container, false);

        myBlockedNumbersList = (RecyclerView) view.findViewById(R.id.blocked_numbers_list);
        myBlockedNumbersList.setHasFixedSize(true);
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        myBlockedNumbersList.setLayoutManager(lm);
        myBlockedNumbersList.setAdapter(myBlockedNumbersAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        myBlockedNumbersAdapter.swapCursor(PhoneNumber.listAll(myReadbableDb));
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        myReadbableDb = null;
        super.onDestroy();
    }

    public void refreshList() {
        myBlockedNumbersAdapter.swapCursor(PhoneNumber.listAll(myReadbableDb));
    }
}
