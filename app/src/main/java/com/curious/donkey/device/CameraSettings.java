package com.curious.donkey.device;

import android.annotation.TargetApi;
import android.hardware.Camera;
import android.hardware.Camera.Area;
import android.hardware.Camera.Parameters;
import android.os.Build;

import com.curious.donkey.utils.CameraUtils;

import java.util.List;

/**
 * Created by lulala on 9/4/16.
 */
public class CameraSettings {

    public static void updateFocusMode(Parameters parameters, String focusMode) {
        if (CameraUtils.isSupported(focusMode, parameters.getSupportedFocusModes())) {
            parameters.setFocusMode(focusMode);
        }
    }

    public static void updateSceneMode(Parameters parameters, String sceneMode) {
        if (CameraUtils.isSupported(sceneMode, parameters.getSupportedSceneModes())) {
            parameters.setSceneMode(sceneMode);
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static void updateFocusArea(Parameters parameters, List<Area> areas) {

        if (parameters.getMaxNumFocusAreas() <= 0) {
            return;
        }

        parameters.setFocusAreas(areas);

    }

    public static Camera.Size getMaxPictureSizeSupported(Parameters mParameters) {
        Camera.Size target = null;
        int area = Integer.MIN_VALUE;
        List<Camera.Size> sizes = mParameters.getSupportedPictureSizes();

        for (Camera.Size s : sizes) {
            int result = s.width * s.height;
            if (result > area) {
                area = result;
                target = s;
            }
        }
        return target;
    }


    public static void updatePictureSize(Parameters mParameters, Camera.Size size) {
        Camera.Size target = null;
        int area = Integer.MIN_VALUE;
        List<Camera.Size> sizes = mParameters.getSupportedPictureSizes();

        for (Camera.Size s : sizes) {
            int result = s.width * s.height;
            if (result > area) {
                area = result;
                target = s;
            }
        }

        if (target != null) {
            mParameters.setPictureSize(target.width, target.height);
        }
    }
}
