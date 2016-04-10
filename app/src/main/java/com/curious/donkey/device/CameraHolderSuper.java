package com.curious.donkey.device;

import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Area;
import android.hardware.Camera.Parameters;
import android.os.Build;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;

import com.curious.donkey.utils.CameraUtils;
import com.curious.support.logger.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by lulala on 9/4/16.
 */
public class CameraHolderSuper implements SurfaceHolder.Callback {
    public final static int STATUS_DEFAULT = -1;
    public final static int STATUS_RELEASED = -2;
    public final static int STATUS_OPENED = 0;
    public final static int STATUS_PREVIEWING = 1;
    public final static int STATUS_PAUSING = 2;
    final static String TAG = "CameraHolderSuper";
    private int mStatus = STATUS_DEFAULT;
    private Camera mCamera;
    private int mCameraId = Integer.MIN_VALUE;

    private OnCameraEventListener mListener;


    public void setOnCameraEventListener(OnCameraEventListener l) {
        mListener = l;

        notifyInitializedEvent();
    }

    private void notifyInitializedEvent() {
        if (mListener == null) {
            return;
        }

        if (isCameraAvailable()) {
            mListener.onCameraInitialized(mCamera, mCameraId);
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mCamera = open(CameraOnDevice.BACK_CAMERA_ID);
        notifyInitializedEvent();
    }

    private void updatePreviewDisplay(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);
        } catch (IOException e) {
            Log.e(TAG, "setPreviewDisplay()->", e);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        if (holder.getSurface() == null) {
            return;
        }


        if (mStatus == STATUS_PREVIEWING) {
            mCamera.stopPreview();
        }

        if (isCameraAvailable()) {
            updatePreviewDisplay(holder);

            mCamera.startPreview();
            mStatus = STATUS_PREVIEWING;
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mStatus = STATUS_RELEASED;
        }

        mCamera = null;
        mCameraId = Integer.MIN_VALUE;
    }

    private Camera open(int cameraId) {
        Camera c = null;
        try {
            c = Camera.open(cameraId);
            mCameraId = cameraId;
            mStatus = STATUS_OPENED;

        } catch (Exception e) {
            Log.e(TAG, "open()->", e);
        }
        return c;
    }


    public int getCameraStatus() {
        return mStatus;
    }

    public Camera getCamera() {
        return mCamera;
    }

    private void initializeCamera() {
        if (mCamera == null || mStatus < STATUS_OPENED) {
            return;
        }


        Parameters mParameters = mCamera.getParameters();

        // Reset preview frame rate to the maximum because it may be lowered by
        // video camera application.
        List<Integer> frameRates = mParameters.getSupportedPreviewFrameRates();
        if (frameRates != null) {
            Integer max = Collections.max(frameRates);
            mParameters.setPreviewFrameRate(max);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (!CameraSettings.updateFocusMode(mParameters, Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                CameraSettings.updateFocusMode(mParameters, Parameters.FOCUS_MODE_AUTO);
            }

        } else {
            CameraSettings.updateFocusMode(mParameters, Parameters.FOCUS_MODE_AUTO);
        }



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {

            List<Area> areas = new ArrayList<>();

            areas.add(new Area(new Rect(-100, -200, 100, 200), 800));
            areas.add(new Area(new Rect(-500, -500, -450, -450), 50));
            areas.add(new Area(new Rect(500, -500, 550, -450), 50));
            areas.add(new Area(new Rect(-500, 500, -450, 550), 50));
            areas.add(new Area(new Rect(500, 500, 550, 550), 50));


            CameraSettings.updateFocusArea(mParameters, areas);

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mCamera.enableShutterSound(true);
        }

        mCamera.setParameters(mParameters);
    }

    public boolean isCameraAvailable() {
        return !(mCamera == null || mStatus < STATUS_OPENED);

    }

    public void onFocus(Camera.AutoFocusCallback callback) {
        mCamera.autoFocus(callback);
    }


    public void onSnap(int orientation, int[] pictureOrientation, Camera.ShutterCallback shutter, Camera.PictureCallback raw,
                       Camera.PictureCallback jpeg) {
        if (mStatus == STATUS_PAUSING) {
            return;
        }


        mStatus = STATUS_PAUSING;
        // See android.hardware.Camera.Parameters.setRotation for
        // documentation.
        int rotation = 0;

        Camera.Parameters mParameters = mCamera.getParameters();
        if (orientation != OrientationEventListener.ORIENTATION_UNKNOWN) {
            Camera.CameraInfo info = CameraOnDevice.CAMERA_INFOS[mCameraId];
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                rotation = (info.orientation - orientation + 360) % 360;
            } else {  // back-facing camera
                rotation = (info.orientation + orientation) % 360;
            }
        }

        pictureOrientation[0] = rotation;
        mParameters.setRotation(rotation);
        mCamera.setParameters(mParameters);

        try {
            mCamera.takePicture(shutter, raw, jpeg);
        } catch (Exception e) {
            Log.e(TAG, "onSnap()->", e);
        }
    }

    public void repreivew() {
        if (!isCameraAvailable()) {
            return;
        }

        if (isPausing()) {
            mCamera.startPreview();
            mStatus = STATUS_PREVIEWING;
        }

    }

    public boolean isPausing() {
        return mStatus == STATUS_PAUSING;

    }

    public void cancelFocus() {
        mCamera.cancelAutoFocus();
    }
}
