package li.doerf.leavemealone.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import li.doerf.leavemealone.R;
import li.doerf.leavemealone.db.tables.PhoneNumber;

/**
 * Created by moo on 18/11/15.
 */
public class BlockedNumbersAdapter extends RecyclerViewCursorAdapter<RecyclerViewHolder> {
    private final String LOGTAG = getClass().getSimpleName();

    public BlockedNumbersAdapter(Context aContext, Cursor aCursor) {
        super( aContext, aCursor);
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView itemLayout = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.blocked_number_card, parent, false);
        return new RecyclerViewHolder(itemLayout);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, Cursor aCursor) {
        CardView cardView = (CardView) holder.getView();
        PhoneNumber number = PhoneNumber.create(aCursor);

        TextView numberView = (TextView) cardView.findViewById(R.id.number);
        numberView.setText( number.getNumber());

        TextView nameView = (TextView) cardView.findViewById(R.id.name);
        nameView.setText( number.getName());

        TextView sourceView = (TextView) cardView.findViewById(R.id.source);
        sourceView.setText( number.getSource());
    }
}
