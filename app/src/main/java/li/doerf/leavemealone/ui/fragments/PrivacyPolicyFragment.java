package li.doerf.leavemealone.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import li.doerf.leavemealone.LeaveMeAloneApplication;
import li.doerf.leavemealone.R;

/**
 * Created by moo on 24.02.17.
 */

public class PrivacyPolicyFragment extends Fragment {

    public static final String PRIVACY_POLICY_URL = "http://doerfli.github.io/leavemealone/privacy-policy.html";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.fragment_privacy_policy, container, false);
        WebView webview = (WebView) view.findViewById(R.id.webview);
        webview.loadUrl(PRIVACY_POLICY_URL);
        return view;
    }

}
