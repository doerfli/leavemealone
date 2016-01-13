package li.doerf.leavemealone.ui.fragments;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import li.doerf.leavemealone.R;
import li.doerf.leavemealone.db.AloneSQLiteHelper;
import li.doerf.leavemealone.db.tables.PhoneNumber;
import li.doerf.leavemealone.ui.adapters.BlockedNumbersAdapter;
import li.doerf.leavemealone.ui.adapters.MultiSelector;

/**
 * This fragment show the list of blocked numbers (loaded from the database).
 *
 * Created by moo on 23/11/15.
 */
public class BockedNumbersListFragment extends Fragment implements MultiSelector.SelectableModeListener, BlockedNumbersAdapter.AdapterModelChangedListener {
    private final String LOGTAG = getClass().getSimpleName();
    private String[] myFilteredSources;
    private RecyclerView myBlockedNumbersList;
    private BlockedNumbersAdapter myBlockedNumbersAdapter;
    private SQLiteDatabase myReadbableDb;
    private boolean myShowListMultiselectModeMenu = false;

    public static BockedNumbersListFragment newInstance(String[] aFilteredSources) {
        BockedNumbersListFragment f = new BockedNumbersListFragment();
        f.setFilteredSources(aFilteredSources);
        return f;
    }

    public void setFilteredSources(String[] aFilteredSources) {
        myFilteredSources = aFilteredSources;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        myReadbableDb = AloneSQLiteHelper.getInstance(getContext()).getReadableDatabase();
        myBlockedNumbersAdapter = new BlockedNumbersAdapter(getContext(), null, this, this);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (myShowListMultiselectModeMenu) {
            getFragmentManager().beginTransaction().addToBackStack("multiselect").commit();
            menu.clear();
            inflater.inflate(R.menu.list_multiselect, menu);
        } else {
            super.onCreateOptionsMenu(menu, inflater);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_delete) {
            Log.i(LOGTAG, "delete items selected");
            myBlockedNumbersAdapter.deleteSelectedItems();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        refreshList();
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
        myBlockedNumbersAdapter.swapCursor(PhoneNumber.listAllExcept(myReadbableDb, myFilteredSources));
    }

    @Override
    public void selectableModeChanged(boolean aState) {
        myShowListMultiselectModeMenu = aState;
        getActivity().supportInvalidateOptionsMenu();
    }

    @Override
    public void itemsAdded() {
        refreshList();
        Snackbar.make(getView(), getString(R.string.number_added), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void itemsDeleted() {
        refreshList();
        Snackbar.make(getView(), getString(R.string.numbers_deleted), Snackbar.LENGTH_LONG).show();
    }

    public void backButtonPressed() {
        Log.d(LOGTAG, "got backButtonPressed");
        if (myShowListMultiselectModeMenu) {
            myShowListMultiselectModeMenu = false;
        }
        getActivity().supportInvalidateOptionsMenu();
        myBlockedNumbersAdapter.resetSelectedItems();
    }
}
