package com.curious.donkey.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;

import com.curious.support.logger.Log;
import com.curious.support.utils.StateUtils;

/**
 * Created by Administrator on 2016/4/11.
 */
public class DarkKnightReceiver extends BroadcastReceiver {
    final static String TAG = "DarkKnightReceiver";

    final static int TYPE_DUMMY = Integer.MIN_VALUE;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, intent.toString());
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {

            boolean isNoNetwork = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            Log.d(TAG, "there is no network ? " + isNoNetwork);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                int networkType = intent.getIntExtra(ConnectivityManager.EXTRA_NETWORK_TYPE,
                        TYPE_DUMMY);

                if (networkType == ConnectivityManager.TYPE_WIFI) {
                    Log.d(TAG, "caused by WIFI");
                } else if (networkType == ConnectivityManager.TYPE_MOBILE) {
                    Log.d(TAG, "caused by MOBILE");
                }
            }

            StateUtils.updateNetworkState(context);
        }


    }
}
