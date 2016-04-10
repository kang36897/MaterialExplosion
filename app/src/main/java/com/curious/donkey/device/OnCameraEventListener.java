package com.curious.donkey.device;

import android.hardware.Camera;

/**
 * Created by lulala on 9/4/16.
 */
public interface OnCameraEventListener {
    void onCameraInitialized(Camera camera, int cameraId);
}
