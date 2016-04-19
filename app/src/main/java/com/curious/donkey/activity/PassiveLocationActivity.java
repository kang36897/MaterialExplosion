package com.curious.donkey.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.curious.support.logger.Log;

/**
 * Created by Administrator on 2016/4/19.
 */
public class PassiveLocationActivity extends BaseActivity {

    final static String TAG = "PassiveLocationActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean isGpsOn = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        Log.d(TAG, "isGpsOn = " + isGpsOn);

        boolean isPassiveProviderAvailable = locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);
        Log.d(TAG, "isPassiveProviderAvailable = " + isPassiveProviderAvailable);
        if (isPassiveProviderAvailable) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Log.d(TAG, "passive location = " + locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER));
        }
    }
}
