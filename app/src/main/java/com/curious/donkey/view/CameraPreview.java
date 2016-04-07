package com.curious.donkey.view;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import com.curious.donkey.R;
import com.curious.donkey.device.CameraHolder;
import com.curious.donkey.utils.CameraUtils;
import com.curious.support.logger.Log;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2016/4/7.
 */
public class CameraPreview extends FrameLayout implements SurfaceHolder.Callback, CameraHolder.OnCameraEventListener {

    static final String TAG = "CameraPreview";
    private SurfaceHolder mSurfaceHolder;
    private SurfaceView mPreviewSurface;
    private boolean mIsHolderCreated = false;

    private Camera mCamera;


    public CameraPreview(Context context) {
        this(context, null);
    }

    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.component_camera_preview, this);
        mPreviewSurface = (SurfaceView) findViewById(R.id.preview_surface);

        mSurfaceHolder = mPreviewSurface.getHolder();
        mSurfaceHolder.addCallback(this);
        setKeepScreenOn(true);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

//        initializePictureSize();
//        initializePreviewSize();
    }

    private void initializePreviewSize() {
        if (mCamera == null) {
            return;
        }

        if (getMeasuredWidth() == -1 || getMeasuredHeight() == -1) {
            return;
        }

        Camera.Parameters mParameters = mCamera.getParameters();
        Camera.Size size = mParameters.getPictureSize();
        List<Camera.Size> sizes = mParameters.getSupportedPreviewSizes();
        Camera.Size optimalSize = CameraUtils.getOptimalPreviewSize(sizes, getContext(), size.width, size.height);
        if (optimalSize != null) {
            Camera.Size original = mParameters.getPreviewSize();
            if (!original.equals(optimalSize)) {
                mParameters.setPreviewSize(optimalSize.width, optimalSize.height);

                // Zoom related settings will be changed for different preview
                // sizes, so set and read the parameters to get latest values
                mCamera.setParameters(mParameters);
            }
        }
    }

    private void initializePictureSize() {
        if (mCamera == null) {
            return;
        }
        // set picture size according to the preview size
        Camera.Parameters mParameters = mCamera.getParameters();
        List<Camera.Size> sizes = mParameters.getSupportedPictureSizes();

        Camera.Size optimalSize = null;
        int area = Integer.MIN_VALUE;

        for (Camera.Size size : sizes) {
            int result = size.width * size.height;
            if (result > area) {
                area = result;
                optimalSize = size;
            }

        }

        Camera.Size original = mParameters.getPreviewSize();
        if (!original.equals(optimalSize)) {
            mParameters.setPreviewSize(optimalSize.width, optimalSize.height);

            // Zoom related settings will be changed for different preview
            // sizes, so set and read the parameters to get latest values
            mCamera.setParameters(mParameters);
        }

    }


    private void initializeFrameRate() {

        Camera.Parameters mParameters = mCamera.getParameters();
        List<Integer> frameRates = mParameters.getSupportedPreviewFrameRates();
        if (frameRates != null) {
            Integer max = Collections.max(frameRates);
            mParameters.setPreviewFrameRate(max);
        }
        mCamera.setParameters(mParameters);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    private void startPreview() {
        try {

            mCamera.setPreviewDisplay(mSurfaceHolder);
            CameraHolder.getInstance().startPreview();

        } catch (IOException e) {
            Log.e(TAG, "startPreview()->", e);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mSurfaceHolder = holder;
        mIsHolderCreated = true;
        if (mCamera == null) {
            return;
        }

        startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mIsHolderCreated = false;
        mSurfaceHolder = null;
    }

    @Override
    public void onCameraInitialized(Camera camera, int cameraId) {
        mCamera = camera;

        initializeFrameRate();
        initializePictureSize();
        initializePreviewSize();
        initializeSceneMode();
        initializeAutoFocus();

        if (mIsHolderCreated)
            startPreview();
    }

    @Override
    public void onCameraPreviewed(Camera camera, int cameraId) {
        camera.autoFocus(new Camera.AutoFocusCallback() {

            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                Log.d(TAG, "onAutoFocus()-> success =" + success);

            }
        });
    }

    @Override
    public void onCameraReleased(Camera camera, int cameraId) {
        if (mCamera == camera) {
            mCamera = null;
        }
    }

    private void initializeSceneMode() {

        if (mCamera == null) {
            return;
        }

        Camera.Parameters mParameters = mCamera.getParameters();

        String mSceneMode = Camera.Parameters.SCENE_MODE_HDR;
        if (CameraUtils.isSupported(mSceneMode, mParameters.getSupportedSceneModes())) {
            mParameters.setSceneMode(mSceneMode);
        } else {
            mSceneMode = Camera.Parameters.SCENE_MODE_AUTO;

            if (CameraUtils.isSupported(mSceneMode, mParameters.getSupportedSceneModes())) {
                mParameters.setSceneMode(mSceneMode);
            }
        }


        mCamera.setParameters(mParameters);
    }

    private void initializeAutoFocus() {
        if (mCamera == null) {
            return;
        }

        Camera.Parameters mParameters = mCamera.getParameters();
        String mFocusMode = mParameters.getFocusMode();
        Log.d(TAG, "initializeAutoFocus()-> mFocusMode = " + mFocusMode);

        if (mFocusMode == null) {
            mFocusMode = Camera.Parameters.FOCUS_MODE_AUTO;
        } else {
            mFocusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE;
        }

        if (CameraUtils.isSupported(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE, mParameters.getSupportedFocusModes())) {
            mParameters.setFocusMode(mFocusMode);
        }

        mCamera.setParameters(mParameters);
    }


}
