package com.curious.donkey.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ExifInterface;

import com.curious.donkey.data.MImage;
import com.curious.support.logger.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by lulala on 9/4/16.
 */
public class ImageUtils {
    final static String TAG = "ImageUtils";

    public static void saveImage(byte[] data, File image) {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(image);
            outputStream.write(data);
            outputStream.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "onPictureTaken()->", e);
        } catch (IOException e) {
            Log.e(TAG, "onPictureTaken()->", e);
        }
    }

    public static void clipImage(byte[] data, Point[] sensitiveAreas, int rotateDegree, MImage mImage) {
        File image;
        FileOutputStream outputStream;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inScaled = false;
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, opts);
        int imgWidth = opts.outWidth;
        int imgHeight = opts.outHeight;

        opts.inJustDecodeBounds = false;
        Bitmap source = BitmapFactory.decodeByteArray(data, 0, data.length, opts);

        int x, y, wd, wh;
        float wRate = (float) sensitiveAreas[1].x / sensitiveAreas[0].x;
        float hRate = (float) sensitiveAreas[1].y / sensitiveAreas[0].y;

        if (imgWidth > imgHeight) {
            wd = (int) (hRate * imgWidth);
            wh = (int) (wRate * imgHeight);
        } else {
            wd = (int) (wRate * imgWidth);
            wh = (int) (hRate * imgHeight);
        }

        x = (imgWidth - wd) / 2;
        y = (imgHeight - wh) / 2;

        Matrix m = new Matrix();
        m.setRotate(rotateDegree);
        Bitmap clippedOne = Bitmap.createBitmap(source, x, y, wd, wh, m, false);
        source.recycle();

        image = StorageUtil.getOutputMediaFile(StorageUtil.MEDIA_TYPE_IMAGE);
        try {
            outputStream = new FileOutputStream(image);
            clippedOne.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            outputStream.close();

            if (mImage == null) {
                clippedOne.recycle();
            } else {
                mImage.mBitmap = clippedOne;
                mImage.mImgPath = image.getAbsolutePath();
            }


        } catch (FileNotFoundException e) {
            Log.e(TAG, "onPictureTaken()->", e);
        } catch (IOException e) {
            Log.e(TAG, "onPictureTaken()->", e);
        }


    }
}
