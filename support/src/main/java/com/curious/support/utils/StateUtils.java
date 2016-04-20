package com.curious.support.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.curious.support.data.NetworkState;

/**
 * Created by Administrator on 2016/3/16.
 */
public class StateUtils {

    public static boolean isInternetConnectionAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mActiveNetwork = manager.getActiveNetworkInfo();
        return mActiveNetwork.isConnectedOrConnecting();
    }

    public static boolean isInternetAccessible(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mActiveNetwork = manager.getActiveNetworkInfo();

        return mActiveNetwork != null && mActiveNetwork.isConnected();
    }


    public static NetworkState updateNetworkState(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo mActiveNetwork = manager.getActiveNetworkInfo();
        NetworkState state = new NetworkState();
        if (mActiveNetwork == null) {
            state.mIsMobileConnected = false;
            state.mIsWifiConnected = false;
        } else {
            state.mIsMobileConnected = mActiveNetwork.isConnected()
                    && mActiveNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
            state.mIsWifiConnected = mActiveNetwork.isConnected()
                    && mActiveNetwork.getType() == ConnectivityManager.TYPE_WIFI;

        }
        return state;
    }
}
