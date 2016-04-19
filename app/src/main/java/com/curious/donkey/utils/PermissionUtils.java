package com.curious.donkey.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.curious.donkey.data.PermissionRequestHint;
import com.curious.support.logger.Log;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/4/19.
 */
public class PermissionUtils {

    final static String TAG = "PermissionUtils";

    public static boolean checkPermissionGranted(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }


    public static void showRequestPermissionRationale(Activity activity, PermissionRequestHint hint) {
        if (activity.isFinishing()) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (activity.isDestroyed()) {
                return;
            }
        }


        if (hint == null) {
            return;
        }

        hint.show(activity);
    }


    /**
     * Check that the needed permissions are granted. if they have been granted,
     * return true. if not ,return false and request them or show some rationale to user
     * at the same time.
     *
     * @param activity
     * @param permissions must be in the same permission group
     * @param requestCode
     * @param hint
     * @return
     */
    public static boolean checkNeededPermissionGranted(final Activity activity, String[] permissions,
                                                       int requestCode, final PermissionRequestHint hint) {
        if (permissions == null || permissions.length == 0) {
            return true;
        }

        ArrayList<String> deniedPermission = new ArrayList<>();

        for (String per : permissions) {
            if (checkPermissionGranted(activity, per)) {
                Log.d(TAG, per + " has been granted!");
            } else {
                deniedPermission.add(per);
            }
        }

        if (deniedPermission.isEmpty()) {
            return true;
        }


        boolean isRationalNeeded = false;

        for (String per : deniedPermission) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, per)) {
                isRationalNeeded = true;
                break;
            }
        }

        if (isRationalNeeded) {
            //show something to explain you want to have this permission
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showRequestPermissionRationale(activity, hint);
                }
            });
        } else {
            requestNeededPermission(activity, deniedPermission.toArray(new String[0]), requestCode);
        }

        return false;

    }

    public static void requestNeededPermission(Activity activity, String[] neededPermission, int requestCode) {
        ActivityCompat.requestPermissions(activity, neededPermission,
                requestCode);
    }
}
