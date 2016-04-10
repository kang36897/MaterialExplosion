package com.curious.donkey.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.OrientationEventListener;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;

import com.curious.donkey.R;
import com.curious.donkey.data.MImage;
import com.curious.donkey.device.CameraHolderS;
import com.curious.donkey.device.CameraSettings;
import com.curious.donkey.device.OnCameraEventListener;
import com.curious.donkey.fragment.ImageReviewFragment;
import com.curious.donkey.utils.CameraUtils;
import com.curious.donkey.utils.ImageUtils;
import com.curious.donkey.view.PreviewFrame;
import com.curious.donkey.view.ShutterButton;
import com.curious.support.logger.Log;

/**
 * Created by lulala on 9/4/16.
 */
public class CameraOnMainLoopActivity extends AppCompatActivity implements OnCameraEventListener,
        ShutterButton.OnShutterButtonListener, ImageReviewFragment.OnImageActionListener {

    private static final String TAG = "CameraOnMainLoopActivity";
    private static final int FOCUS_NOT_STARTED = 0;
    private static final int FOCUSING = 1;
    private static final int FOCUSING_SNAP_ON_FINISH = 2;
    private static final int FOCUS_SUCCESS = 3;
    private static final int FOCUS_FAIL = 4;
    private static final int FOCUS_BEEP_VOLUME = 100;
    private final static String FRAGMENT_TAG = "image_review";
    private SurfaceView mSurfaceView;
    private CameraHolderS mCameraHolder;
    private ShutterButton mShutterBtn;
    private OrientationEventListener mOrientationEventListener;
    private int mOrientation = OrientationEventListener.ORIENTATION_UNKNOWN;
    private int[] mPictureOrientation = new int[]{
            OrientationEventListener.ORIENTATION_UNKNOWN};
    private int mFocusState = FOCUS_NOT_STARTED;
    private ToneGenerator mFocusToneGenerator;
    private View mClipArea;
    private Point[] mSensitiveAreas = new Point[2];
    private Camera.AutoFocusCallback mAutoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean focused, Camera camera) {
            if (mFocusState == FOCUSING_SNAP_ON_FINISH) {
                if (focused) {
                    mFocusState = FOCUS_SUCCESS;
                } else {
                    mFocusState = FOCUS_FAIL;
                }
                //take photo
                mCameraHolder.onSnap(mOrientation, mPictureOrientation, mShutterCallBack, null, mPictureCallback);
            } else if (mFocusState == FOCUSING) {
                // User is half-pressing the focus key. Play the focus tone.
                // Do not take the picture now.
                ToneGenerator tg = mFocusToneGenerator;
                if (tg != null) {
                    tg.startTone(ToneGenerator.TONE_PROP_BEEP2);
                }

                if (focused) {
                    mFocusState = FOCUS_SUCCESS;
                } else {
                    mFocusState = FOCUS_FAIL;
                }
            } else if (mFocusState == FOCUS_NOT_STARTED) {
                // User has released the focus key before focus completes.
                // Do nothing.
            }
        }
    };
    private ProgressDialog mProgressDialog;
    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(final byte[] data, Camera camera) {
            //clip the img
            new Thread() {
                @Override
                public void run() {
                    final MImage bucket = new MImage();
                    ImageUtils.clipImage(data, mSensitiveAreas, mPictureOrientation[0], bucket);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (mProgressDialog.isShowing()) {
                                mProgressDialog.dismiss();
                            }
                            reviewImageSliced(bucket);

                        }
                    });
                }
            }.start();

        }
    };
    private Camera.ShutterCallback mShutterCallBack = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            mFocusState = FOCUS_NOT_STARTED;
            if (mProgressDialog.isShowing()) {
                return;
            }

            mProgressDialog.show();
        }
    };

    public void reviewImageSliced(MImage bucket) {

        Bundle args = new Bundle();
        args.putString(ImageReviewFragment.SOURCE_IMG_PATH, bucket.mImgPath);
        args.putParcelable(ImageReviewFragment.SOURCE_IMG, bucket.mBitmap);

        Fragment fragment = ImageReviewFragment.getInstance(args);
        getSupportFragmentManager().beginTransaction().add(R.id.image_review_container, fragment, FRAGMENT_TAG).commit();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_on_main_loop);

        mCameraHolder = new CameraHolderS();
        mCameraHolder.setOnCameraEventListener(this);

        mSurfaceView = (SurfaceView) findViewById(R.id.preview_surface);
        mSurfaceView.getHolder().addCallback(mCameraHolder);

        mShutterBtn = (ShutterButton) findViewById(R.id.shutter);
        mShutterBtn.setOnShutterButtonListener(this);

        mClipArea = findViewById(R.id.clip_area);
        mClipArea.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                mSensitiveAreas[0] = new Point();
                mSensitiveAreas[0].x = ((View) mClipArea.getParent()).getWidth();
                mSensitiveAreas[0].y = ((View) mClipArea.getParent()).getHeight();
                mSensitiveAreas[1] = new Point();
                mSensitiveAreas[1].x = mClipArea.getMeasuredWidth();
                mSensitiveAreas[1].y = mClipArea.getMeasuredHeight();

            }
        });

        mOrientationEventListener = new InternalOrientationEventListener(this);
        mOrientationEventListener.enable();

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getString(R.string.save_img_in_progress));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        mOrientationEventListener.enable();
        initializeFocusTone();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
        mOrientationEventListener.disable();
        if (mFocusToneGenerator != null) {
            mFocusToneGenerator.release();
            mFocusToneGenerator = null;
        }

        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onCameraInitialized(Camera camera, int cameraId) {
        CameraUtils.setCameraDisplayOrientation(this, cameraId, camera);

        Parameters mParameters = camera.getParameters();

        Size pictureSize = CameraSettings.getMaxPictureSizeSupported(mParameters);
        if (pictureSize != null) {
            mParameters.setPictureSize(pictureSize.width, pictureSize.height);
        }


        // change the frame size by picture size
        pictureSize = mParameters.getPictureSize();
        float ratio = (float) pictureSize.width / pictureSize.height;
        // always keep the width is the smallest
        ratio = ratio > 1 ? 1 / ratio : ratio;
        PreviewFrame frame = (PreviewFrame) findViewById(R.id.preview_frame);
        frame.setAspectRatio(ratio);


        Size previewSize = CameraUtils.getOptimalPreviewSize(mParameters.getSupportedPreviewSizes(),
                this, pictureSize.width, pictureSize.height);

        if (!previewSize.equals(mParameters.getPreviewSize())) {
            mParameters.setPreviewSize(previewSize.width, previewSize.height);
        }


        camera.setParameters(mParameters);

    }


    private void initializeFocusTone() {
        // Initialize focus tone generator.
        try {
            mFocusToneGenerator = new ToneGenerator(
                    AudioManager.STREAM_SYSTEM, FOCUS_BEEP_VOLUME);
        } catch (Throwable ex) {
            Log.w(TAG, "Exception caught while creating tone generator: ", ex);
            mFocusToneGenerator = null;
        }
    }

    @Override
    public void onShutterButtonFocus(ShutterButton b, boolean pressed) {

        if (!mCameraHolder.isCameraAvailable() || mCameraHolder.isPausing()) {
            return;
        }

        if (pressed) {

            if (mFocusState == FOCUSING) {
                return;
            }

            mFocusState = FOCUSING;
            mCameraHolder.onFocus(mAutoFocusCallback);
        } else {
            mCameraHolder.cancelFocus();
        }
    }

    @Override
    public void onShutterButtonClick(ShutterButton b) {
        if (!mCameraHolder.isCameraAvailable() || mCameraHolder.isPausing()) {
            return;
        }

        if (mFocusState == FOCUS_SUCCESS || mFocusState == FOCUS_FAIL) {
            //take picture
            mCameraHolder.onSnap(mOrientation, mPictureOrientation, mShutterCallBack, null, mPictureCallback);
        } else if (mFocusState == FOCUSING) {
            mFocusState = FOCUSING_SNAP_ON_FINISH;

        } else {

        }

    }

    @Override
    public void onImageReceived(boolean kept, String path) {

        //TODO you can do something to the path
        if (kept) {

        } else {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();

            mCameraHolder.repreivew();

        }

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
