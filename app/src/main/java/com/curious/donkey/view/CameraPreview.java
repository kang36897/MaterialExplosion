package com.curious.donkey.view;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import com.curious.donkey.R;
import com.curious.donkey.device.CameraHolder;
import com.curious.donkey.utils.CameraUtils;
import com.curious.support.logger.Log;

import java.io.IOException;
import java.util.ArrayList;
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
    private View mClipArea;

    private Point[] mSensitiveAreas;

    public CameraPreview(Context context) {
        this(context, null);
    }

    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.component_camera_preview, this);
        mPreviewSurface = (SurfaceView) findViewById(R.id.preview_surface);
        mClipArea = findViewById(R.id.clip_area);

        mSurfaceHolder = mPreviewSurface.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        setKeepScreenOn(true);

        mSensitiveAreas = new Point[2];
        mSensitiveAreas[0] = new Point();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        mSensitiveAreas[0].x = getMeasuredWidth();
        mSensitiveAreas[0].y = getMeasuredHeight();
        mSensitiveAreas[1].x = mClipArea.getMeasuredWidth();
        mSensitiveAreas[1].y = mClipArea.getMeasuredHeight();

    }

    public Point[] getClip() {
        return mSensitiveAreas;
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
        Log.d(TAG, "surfaceCreated()");
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
        Log.d(TAG, "surfaceChanged()");
        if (mSurfaceHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }
        mIsHolderCreated = true;

        if (mCamera == null) {
            return;
        }

        startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed()");
        mIsHolderCreated = false;
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

        mFocusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE;
        if (CameraUtils.isSupported(mFocusMode, mParameters.getSupportedFocusModes())) {
            mParameters.setFocusMode(mFocusMode);
        } else {
            mFocusMode = Camera.Parameters.FOCUS_MODE_AUTO;
            if (CameraUtils.isSupported(mFocusMode, mParameters.getSupportedFocusModes())) {
                mParameters.setFocusMode(mFocusMode);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            int maxFocusAreas = mParameters.getMaxNumFocusAreas();
            if (maxFocusAreas > 0) {
                List<Camera.Area> areas = new ArrayList<>();
                Rect rect = new Rect(-100, -200, 100, 200);
                areas.add(new Camera.Area(rect, 800));

                if (maxFocusAreas > 1) {
                    rect = new Rect(200, -200, 300, -100);
                    areas.add(new Camera.Area(rect, 200));
                }

                mParameters.setFocusAreas(areas);
            }
        }

        mCamera.setParameters(mParameters);
    }


}
