package li.doerf.leavemealone.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import li.doerf.leavemealone.R;

/**
 * Created by moo on 20/12/15.
 */
public class AboutFragment extends Fragment {
    private final String LOGTAG = getClass().getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.fragment_about, container, false);
        return view;
    }

}
