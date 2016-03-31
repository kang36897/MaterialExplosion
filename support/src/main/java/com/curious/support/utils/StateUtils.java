package com.curious.support.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Administrator on 2016/3/16.
 */
public class StateUtils {

    public static boolean isInternetConnectionAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mActiveNetwork = manager.getActiveNetworkInfo();
        return mActiveNetwork.isConnectedOrConnecting();
    }
}
