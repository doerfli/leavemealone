package li.doerf.leavemealone.ui.adapters;

import android.util.Log;
import android.util.SparseBooleanArray;

import com.google.common.collect.Lists;

import java.util.Collection;

/**
 * Created by moo on 13/12/15.
 */
public class MultiSelector {
    private final String LOGTAG = getClass().getSimpleName();
    private final RecyclerViewCursorAdapter myAdapter;
    private final SelectableModeListener myFragment;

    private SparseBooleanArray mSelectedPositions = new SparseBooleanArray();
    private boolean mIsSelectable = false;

    public MultiSelector(RecyclerViewCursorAdapter adapter, SelectableModeListener selectableModeListener) {
        myAdapter = adapter;
        myFragment = selectableModeListener;
    }

    public void setItemChecked(int position, boolean isChecked) {
        mSelectedPositions.put(position, isChecked);
        myAdapter.notifyDataSetChanged();
        Log.d(LOGTAG, "pos: " + position + " selected: " + isChecked);
    }

    public boolean isItemChecked(int position) {
        return mSelectedPositions.get(position);
    }

    public void setSelectable(boolean selectable) {
        mIsSelectable = selectable;
        myAdapter.notifyDataSetChanged();
        Log.d(LOGTAG, "selectable: " + selectable);
        myFragment.selectableModeChanged(selectable);
    }

    public boolean isSelectable() {
        return mIsSelectable;
    }

    public void resetCheckedItems() {
        mSelectedPositions.clear();
        myAdapter.notifyDataSetChanged();
    }

    public Collection<Integer> getSelectedPositions() {
        Collection<Integer> ids = Lists.newArrayList();

        for (int i = 0; i < mSelectedPositions.size(); i++) {
            if (mSelectedPositions.valueAt(i)) {
                ids.add(mSelectedPositions.keyAt(i));
            }
        }

        return ids;
    }

    /**
     * Interface used to notify a listener when the selectale mode changed
     */
    public interface SelectableModeListener {
        /**
         *
         * @param aState <code>true</code> when list items are selectable, <code>false</code> otherwise.
         */
        void selectableModeChanged(boolean aState);
    }
}
