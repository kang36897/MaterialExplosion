package com.curious.support.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2016/4/12.
 */
public class NetworkState implements Parcelable {
    public boolean mIsWifiConnected = false;
    public boolean mIsMobileConnected = false;

    protected NetworkState(Parcel in) {
        mIsWifiConnected = in.readByte() != 0;
        mIsMobileConnected = in.readByte() != 0;
    }

    public NetworkState() {

    }

    public static final Creator<NetworkState> CREATOR = new Creator<NetworkState>() {
        @Override
        public NetworkState createFromParcel(Parcel in) {
            return new NetworkState(in);
        }

        @Override
        public NetworkState[] newArray(int size) {
            return new NetworkState[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (mIsWifiConnected ? 1 : 0));
        dest.writeByte((byte) (mIsMobileConnected ? 1 : 0));
    }
}
