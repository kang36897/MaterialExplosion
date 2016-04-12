package com.curious.donkey.activity;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.Button;

import com.curious.donkey.R;
import com.curious.donkey.device.CameraHolder;
import com.curious.donkey.utils.CameraUtils;
import com.curious.donkey.utils.ImageUtils;
import com.curious.donkey.utils.StorageUtil;
import com.curious.donkey.view.CameraPreview;
import com.curious.support.logger.Log;

import java.io.File;

/**
 * Created by Administrator on 2016/4/7.
 */
public class PhotoTakenActivity extends AppCompatActivity implements CameraHolder.OnCameraEventListener {

    private static final String TAG = "PhotoTakenActivity";
    private CameraPreview mPreview;
    private Button mShutterView;

    private Camera mCamera;
    private OrientationEventListener mOrientationEventListener;
    private int mOrientation = OrientationEventListener.ORIENTATION_UNKNOWN;
    private int mPictureOrientation = OrientationEventListener.ORIENTATION_UNKNOWN;

    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File image = StorageUtil.getOutputMediaFile(StorageUtil.MEDIA_TYPE_IMAGE);
            ImageUtils.saveImage(data, image);

            //clip the img
            Point[] sensitiveAreas = mPreview.getClip();
            ImageUtils.clipImage(data, sensitiveAreas, mPictureOrientation, null);

        }
    };


    private Camera.AutoFocusCallback mAutoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if (!success) {
                mCamera.cancelAutoFocus();
                return;
            }
            //TODO play some sound

            capture();
        }
    };

    private void capture() {

        mPictureOrientation = OrientationEventListener.ORIENTATION_UNKNOWN;
        // See android.hardware.Camera.Parameters.setRotation for
        // documentation.
        int rotation = 0;

        Camera.Parameters mParameters = mCamera.getParameters();
        if (mOrientation != OrientationEventListener.ORIENTATION_UNKNOWN) {
            Camera.CameraInfo info = CameraHolder.getInstance().getCameraInfo();
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                rotation = (info.orientation - mOrientation + 360) % 360;
            } else {  // back-facing camera
                rotation = (info.orientation + mOrientation) % 360;
            }
        }

        mPictureOrientation = rotation;
        mParameters.setRotation(rotation);
        mCamera.setParameters(mParameters);

        try {
            mCamera.takePicture(null, null, mPictureCallback);
        } catch (Exception e) {
            Log.e(TAG, "capture()->", e);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_taken);

        // initialize the camera
        CameraHolder.getInstance().openCamera();
        CameraHolder.getInstance().addOnCameraEventListener(this);

        mPreview = (CameraPreview) findViewById(R.id.camera_preview);
        CameraHolder.getInstance().addOnCameraEventListener(mPreview);

        mShutterView = (Button) findViewById(R.id.shutter);
        mShutterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCamera == null) {
                    return;
                }

                // to check the camera state before take a picture
                if (CameraHolder.getInstance().isIDEL()) {
                    mCamera.autoFocus(mAutoFocusCallback);
                }


            }
        });

        mOrientationEventListener = new InternalOrientationEventListener(this);
        mOrientationEventListener.enable();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        mOrientationEventListener.enable();
        CameraHolder.getInstance().openCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
        mOrientationEventListener.disable();
        CameraHolder.getInstance().releaseCamera();
    }

    @Override
    public void onCameraInitialized(Camera camera, int cameraId) {
        mCamera = camera;
        CameraUtils.setCameraDisplayOrientation(this, cameraId, mCamera);
    }

    @Override
    public void onCameraPreviewed(Camera camera, int cameraId) {
    }

    @Override
    public void onCameraReleased(Camera camera, int cameraId) {
        if (mCamera == camera) {
            mCamera = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPreview != null) {
            CameraHolder.getInstance().removeOnCameraEventListener(mPreview);
            mPreview = null;
        }

        CameraHolder.getInstance().removeOnCameraEventListener(this);
    }

    class InternalOrientationEventListener extends OrientationEventListener {
        public InternalOrientationEventListener(Context context) {
            super(context);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            // We keep the last known orientation. So if the user first orient
            // the camera then point the camera to floor or sky, we still have
            // the correct orientation.
            if (orientation == ORIENTATION_UNKNOWN) return;
            mOrientation = CameraUtils.roundOrientation(orientation);
        }
    }
}
