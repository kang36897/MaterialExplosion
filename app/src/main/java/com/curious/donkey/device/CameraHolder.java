package com.curious.donkey.device;

import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import static android.hardware.Camera.CameraInfo;
import static android.hardware.Camera.open;

import com.curious.donkey.utils.CameraUtils;
import com.curious.support.logger.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/4/7.
 */
public class CameraHolder {
    final static String TAG = "CameraHelper";
    public final static int CAMERA_BACK = 0;
    public final static int CAMERA_FRONT = 1;

    final static int STATUS_DEFAULT = 0;
    final static int STATUS_INITIALIZING = 1;
    final static int STATUS_INITIALIZED = 2;
    final static int STATUS_PREVIEWING = 3;
    final static int STATUS_PAUSING = 4;
    final static int STATUS_RELEASED = 5;

    private int mStatus = STATUS_DEFAULT;

    static final int ID_INITIALIZE_CAMERA = 0x01;
    static final int ID_RELEASE_CAMERA = 0x02;

    private List<OnCameraEventListener> mObservers = new ArrayList<>();
    private CameraInfo cameraInfo;

    public void openCamera() {
        openCamera(CAMERA_BACK);
    }

    public void openCamera(int cameraFacing) {
//        if (mStatus == STATUS_PAUSING) {
//            mInternalHandler.removeMessages(ID_RELEASE_CAMERA);
//
//            mCamera.startPreview();
//            mStatus = STATUS_PREVIEWING;
//            return;
//        }


        if (mStatus == STATUS_INITIALIZING || mStatus == STATUS_INITIALIZED) {
            return;
        }

        mStatus = STATUS_INITIALIZING;
        Message m = mInternalHandler.obtainMessage(ID_INITIALIZE_CAMERA);
        m.arg1 = cameraFacing;
        m.sendToTarget();
    }

    public void releaseCamera() {
        if (mCamera == null) {
            return;
        }

        if (mStatus == STATUS_PREVIEWING) {
            mCamera.stopPreview();
            mStatus = STATUS_PAUSING;
        }

        mInternalHandler.sendEmptyMessage(ID_RELEASE_CAMERA);
//        mInternalHandler.sendEmptyMessageDelayed(ID_RELEASE_CAMERA, 1000 * 30);

    }


    public boolean isIDEL() {
        if (mCamera == null) {
            return false;
        }

        return mStatus == STATUS_PREVIEWING;

    }

    public void startPreview() {
        mCamera.startPreview();

        if (mObservers.isEmpty()) {
            return;
        }

        for (OnCameraEventListener l : mObservers) {
            l.onCameraPreviewed(mCamera, mCameraId);
        }
        mStatus = STATUS_PREVIEWING;
    }

    public CameraInfo getCameraInfo() {
        if (mCamera == null || mCameraId == -1) {
            return null;
        }

        CameraInfo cameraInfo = new CameraInfo();
        Camera.getCameraInfo(mCameraId, cameraInfo);

        return cameraInfo;
    }

    public interface OnCameraEventListener {
        void onCameraInitialized(Camera camera, int cameraId);

        void onCameraPreviewed(Camera camera, int cameraId);

        void onCameraReleased(Camera camera, int cameraId);
    }

    private int mCameraId = -1;
    private Camera mCamera;

    private HandlerThread mHandlerThread;
    private Handler mInternalHandler;

    private CameraHolder() {

        mHandlerThread = new HandlerThread(TAG);
        mHandlerThread.start();

        mInternalHandler = new CameraHandler(mHandlerThread.getLooper());
    }

    static class Singleton {
        public final static CameraHolder CAMERA_HOLDER = new CameraHolder();
    }

    public static CameraHolder getInstance() {
        return Singleton.CAMERA_HOLDER;
    }

    public void addOnCameraEventListener(OnCameraEventListener l) {
        mObservers.add(l);

        if (mCamera == null) {
            return;
        }

        l.onCameraInitialized(mCamera, mCameraId);

    }

    public void removeOnCameraEventListener(OnCameraEventListener l) {
        //TODO do something that you want before stop listening


        mObservers.remove(l);
    }


    Camera obtain(int cameraFacing) {
        try {
            int numberOfCameras = CameraUtils.getAvailableCameras();

            CameraInfo cameraInfo = new CameraInfo();
            for (int i = 0; i < numberOfCameras; i++) {
                Camera.getCameraInfo(i, cameraInfo);

                if (cameraFacing == CAMERA_BACK && cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
                    mCameraId = i;
                    return Camera.open(i);
                } else if (cameraFacing == CAMERA_FRONT && cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT) {
                    mCameraId = i;
                    return Camera.open(i);
                }

            }

            return null;
        } catch (Exception e) {
            Log.e(TAG, "obtain()->", e);
            return null;
        }
    }


    class CameraHandler extends Handler {


        public CameraHandler(Looper looper) {
            super(looper);
        }


        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ID_INITIALIZE_CAMERA:
                    Camera c = obtain(msg.arg1);
                    if (c == null) {
                        mStatus = STATUS_DEFAULT;
                    } else {
                        mStatus = STATUS_INITIALIZED;
                        mCamera = c;
                    }

                    notifyCameraInitializedEvent();

                    break;

                case ID_RELEASE_CAMERA:
                    mCamera.release();
                    mStatus = STATUS_RELEASED;

                    if (mObservers.isEmpty()) {
                        return;
                    }

                    for (OnCameraEventListener l : mObservers) {
                        l.onCameraReleased(mCamera, mCameraId);
                    }

                    mCamera = null;
                    break;

                default:
                    break;
            }
        }

        private void notifyCameraInitializedEvent() {
            if (mStatus != STATUS_INITIALIZED) {
                return;
            }

            if (mObservers.isEmpty()) {
                return;
            }

            for (OnCameraEventListener l : mObservers) {
                l.onCameraInitialized(mCamera, mCameraId);
            }
        }
    }

}
