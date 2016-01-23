package li.doerf.leavemealone.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import li.doerf.leavemealone.util.SynchronizationHelper;

/**
 * Created by pamapa on 21.01.16.
 */
public class NetworkStateChangeReceiver extends BroadcastReceiver {
    private final String LOGTAG = getClass().getSimpleName();

    @Override
    public void onReceive(Context aContext, Intent intent) {
        /*
        ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        boolean isConnected = wifi != null && wifi.isConnectedOrConnecting() ||
                mobile != null && mobile.isConnectedOrConnecting();
        */


        // TODO: this code is wifi depended...

        //WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);


        boolean isConnected = false;
        boolean isWifi = false;
        if (networkInfo != null) {
            Log.d(LOGTAG, "Type:" + networkInfo.getType() + " State:" + networkInfo.getState());
            isConnected = networkInfo.isConnectedOrConnecting();
            isWifi = networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
        }
        Log.d(LOGTAG, "isConnected:" + isConnected + " isWifi:" + isWifi);
        SynchronizationHelper.onNetworkStateChanged(aContext, isConnected, isWifi);
    }
}
