package com.curious.donkey.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.curious.donkey.data.Const;
import com.curious.donkey.data.PermissionRequestHint;
import com.curious.donkey.utils.PermissionUtils;
import com.curious.support.logger.Log;

/**
 * Created by lulala on 31/3/16.
 */
public class BaseActivity extends AppCompatActivity {

    final static String TAG = "BaseActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case Const.REQUEST_PERMISSION_ABOUT_CAMERA:
                // If request is cancelled, the result arrays are empty.

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "request " + permissions[0] + " is granted!");
                } else {
                    // show something to user to know the request permission is denied,
                    // he will not use some function in normal way.
                    PermissionUtils.showRequestPermissionRationale(this,
                            PermissionRequestHint.getHint(Const.REQUEST_PERMISSION_ABOUT_CAMERA,
                                    PermissionRequestHint.TOAST_HINT));
                }

                break;

            default:
                break;
        }

    }
}
