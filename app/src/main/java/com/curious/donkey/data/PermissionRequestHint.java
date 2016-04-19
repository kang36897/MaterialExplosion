package com.curious.donkey.data;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.widget.Toast;

import com.curious.donkey.R;

/**
 * Created by Administrator on 2016/4/19.
 */
public class PermissionRequestHint {

    public final static int TOAST_HINT = 1;
    public final static int DIALOG_HINT = 2;

    public int type = TOAST_HINT;
    public int mTitleRes = Integer.MIN_VALUE;
    public int mContentRes = Integer.MIN_VALUE;

    public String mTitle;
    public String mContent;

    public PermissionRequestHint(int type, int mTitleRes, int mContentRes) {
        this(type, mTitleRes, mContentRes, null);
    }

    public PermissionRequestHint(int type, int mTitleRes, int mContentRes,
                                 DialogInterface.OnClickListener positiveCallback) {
        this.type = type;
        this.mTitleRes = mTitleRes;
        this.mContentRes = mContentRes;
        this.positiveCallback = positiveCallback;
    }

    public PermissionRequestHint(int type, String mTitle, String mContent) {
        this(type, mTitle, mContent, null);
    }

    public PermissionRequestHint(int type, String mTitle, String mContent,
                                 DialogInterface.OnClickListener positiveCallback) {
        this.type = type;
        this.mTitle = mTitle;
        this.mContent = mContent;
        this.positiveCallback = positiveCallback;
    }

    public DialogInterface.OnClickListener positiveCallback;

    public static PermissionRequestHint getHint(int requestCode, int hintType) {

        switch (requestCode) {
            case Const.REQUEST_PERMISSION_ABOUT_CAMERA:
                return new PermissionRequestHint(hintType,
                        R.string.hint_request_camera_permission_title,
                        R.string.hint_request_camera_permission_content);


            case Const.REQUEST_PERMISSION_ABOUT_LOCATION:

                return new PermissionRequestHint(hintType,
                        R.string.hint_request_location_permission_title,
                        R.string.hint_request_location_permission_content);

            case Const.REQUEST_PERMISSION_ABOUT_STORAGE:
                return new PermissionRequestHint(hintType,
                        R.string.hint_request_storage_permission_title,
                        R.string.hint_request_storage_permission_content);

            default:
                return new PermissionRequestHint();

        }

    }

    public PermissionRequestHint() {

    }


    public void show(Activity activity) {
        if (type == TOAST_HINT) {
            showToast(activity);
        } else if (type == DIALOG_HINT) {
            showDialog(activity);
        }

    }

    private void showDialog(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (mTitleRes == Integer.MIN_VALUE) {
            if (TextUtils.isEmpty(mTitle)) {
            } else {
                builder.setTitle(mTitle);
            }
        } else {
            builder.setTitle(mTitleRes);
        }

        if (mContentRes == Integer.MIN_VALUE) {
            if (TextUtils.isEmpty(mContent)) {
                return;
            }

            builder.setMessage(mContent);
        } else {
            builder.setMessage(mContentRes);
        }

        if (positiveCallback == null) {
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        } else {
            builder.setPositiveButton(R.string.ok, positiveCallback);
        }

        builder.show();
    }

    private void showToast(Activity activity) {
        if (mContentRes == Integer.MIN_VALUE) {
            if (TextUtils.isEmpty(mContent)) {
                return;
            } else {
                Toast.makeText(activity, mContent, Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(activity, mContentRes, Toast.LENGTH_LONG).show();
        }
    }
}
