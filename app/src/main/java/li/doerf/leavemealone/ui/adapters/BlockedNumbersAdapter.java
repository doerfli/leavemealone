package li.doerf.leavemealone.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.collect.Lists;

import java.util.List;

import li.doerf.leavemealone.R;
import li.doerf.leavemealone.db.AloneSQLiteHelper;
import li.doerf.leavemealone.db.tables.PhoneNumber;

/**
 * Created by moo on 18/11/15.
 */
public class BlockedNumbersAdapter extends RecyclerViewCursorAdapter<RecyclerViewHolder> {
    private final String LOGTAG = getClass().getSimpleName();
    private final MultiSelector myMultiSelector;
    private final AdapterModelChangedListener myItemsChangedListener;

    public BlockedNumbersAdapter(Context aContext, Cursor aCursor, MultiSelector.SelectableModeListener aFragmentShowing, AdapterModelChangedListener anItemsChangesListener) {
        super( aContext, aCursor);
        myMultiSelector = new MultiSelector( this, aFragmentShowing);
        myItemsChangedListener = anItemsChangesListener;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView itemLayout = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.blocked_number_card, parent, false);
        return new RecyclerViewHolder(itemLayout);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, Cursor aCursor) {
        CardView cardView = (CardView) holder.getView();

        if ( myMultiSelector.isSelectable() && myMultiSelector.isItemChecked( holder.getAdapterPosition())) {
            cardView.setBackgroundColor( getContext().getResources().getColor( R.color.colorAccent));
        } else {
            cardView.setBackgroundColor( getContext().getResources().getColor( R.color.cardview_light_background));
        }


        PhoneNumber number = PhoneNumber.create(aCursor);

        TextView numberView = (TextView) cardView.findViewById(R.id.number);
        numberView.setText( number.getNumber());

        TextView nameView = (TextView) cardView.findViewById(R.id.name);
        nameView.setText( number.getName());

        TextView sourceView = (TextView) cardView.findViewById(R.id.source);
        sourceView.setText( number.getSource());

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myMultiSelector.isSelectable()) {
                    int p = holder.getAdapterPosition();
                    myMultiSelector.setItemChecked(p, !myMultiSelector.isItemChecked(p));
                }
            }
        });

        cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int p = holder.getAdapterPosition();
                myMultiSelector.setSelectable(!myMultiSelector.isSelectable());

                if (myMultiSelector.isSelectable()) {
                    myMultiSelector.setItemChecked(p, !myMultiSelector.isItemChecked(p));
                } else {
                    myMultiSelector.resetCheckedItems();
                }

                return true;
            }
        });
    }

    public void deleteSelectedItems() {
        List<PhoneNumber> numbersToDelete = Lists.newArrayList();
        Cursor cursor = getCursor();

        for ( int i : myMultiSelector.getSelectedPositions()) {
            cursor.moveToPosition(i);
            PhoneNumber n = PhoneNumber.create( cursor);
            numbersToDelete.add(n);
        }

        resetSelectedItems();

        SQLiteDatabase db = AloneSQLiteHelper.getInstance( getContext()).getWritableDatabase();
        for ( PhoneNumber num : numbersToDelete ) {
            Log.d(LOGTAG, "Deleting " + num.getNumber());
            num.delete(db);
        }

        myItemsChangedListener.itemsDeleted();
    }

    public void resetSelectedItems() {
        Log.d(LOGTAG, "resetting selected items");
        myMultiSelector.setSelectable(false);
        myMultiSelector.resetCheckedItems();
        notifyDataSetChanged();
    }

    public interface AdapterModelChangedListener {
        void itemsAdded();
        void itemsDeleted();
    }
}
