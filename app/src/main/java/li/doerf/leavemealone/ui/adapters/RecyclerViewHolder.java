package li.doerf.leavemealone.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by moo on 31/01/15.
 */
class RecyclerViewHolder extends RecyclerView.ViewHolder {

    private final View myView;

    RecyclerViewHolder(View itemView) {
        super(itemView);
        myView = itemView;
    }

    View getView()
    {
        return myView;
    }
}
