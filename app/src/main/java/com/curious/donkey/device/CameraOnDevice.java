package com.curious.donkey.device;

import android.hardware.Camera;

/**
 * Created by lulala on 9/4/16.
 */
public class CameraOnDevice {
    public final static int AVAILABLE_CAMERA_NUMS = Camera.getNumberOfCameras();

    public static int BACK_CAMERA_ID = Integer.MIN_VALUE;
    public static int FRONT_CAMERA_ID = Integer.MIN_VALUE;
    public static Camera.CameraInfo[] CAMERA_INFOS = new Camera.CameraInfo[AVAILABLE_CAMERA_NUMS];

    static {

        for (int i = 0; i < AVAILABLE_CAMERA_NUMS; i++) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(i, cameraInfo);

            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK && BACK_CAMERA_ID == Integer.MIN_VALUE) {
                BACK_CAMERA_ID = i;
                CAMERA_INFOS[i] = cameraInfo;
            } else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT && FRONT_CAMERA_ID == Integer.MIN_VALUE) {
                FRONT_CAMERA_ID = i;
                CAMERA_INFOS[i] = cameraInfo;
            }

        }

    }
}
