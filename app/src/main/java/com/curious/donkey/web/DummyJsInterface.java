package com.curious.donkey.web;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.webkit.JavascriptInterface;

import com.curious.donkey.R;

/**
 * Created by Administrator on 2016/4/5.
 */
public class DummyJsInterface {

    private Activity mHostActivity;

    public DummyJsInterface(Activity activity) {
        mHostActivity = activity;
    }


    @JavascriptInterface
    public void showAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mHostActivity);
        builder.setTitle(title).setMessage(message).setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();


    }
}
