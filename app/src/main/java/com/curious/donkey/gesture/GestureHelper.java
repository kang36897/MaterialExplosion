package com.curious.donkey.gesture;

import android.content.Context;
import android.support.v4.view.VelocityTrackerCompat;
import android.view.MotionEvent;
import android.view.VelocityTracker;

/**
 * Created by lulala on 4/4/16.
 */
public class GestureHelper {

    final static int STATUS_NORMAL = 0;
    final static int STATUS_FLING = 1;
    private int mStatus;


    private Context mContext;
    private VelocityTracker mVelocityTracker;

    public GestureHelper(Context context) {
        mContext = context;
        mVelocityTracker = VelocityTracker.obtain();
    }


    public boolean onTouchEvent(MotionEvent motionEvent) {

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:

                break;


            case MotionEvent.ACTION_MOVE:

                break;

            case MotionEvent.ACTION_POINTER_DOWN:


                break;

            case MotionEvent.ACTION_UP:


                break;

            case MotionEvent.ACTION_CANCEL:

                break;

        }

        return false;
    }


    public interface OnHorizontalFlingListener {

        boolean onFlingLeft(MotionEvent e1, MotionEvent e2, float velocityX);

        boolean onFlingRight(MotionEvent e1, MotionEvent e2, float velocityX);

    }

}
