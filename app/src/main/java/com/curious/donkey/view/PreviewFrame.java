package com.curious.donkey.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.curious.donkey.R;

/**
 * Created by lulala on 9/4/16.
 */
public class PreviewFrame extends FrameLayout {

    private double mAspectRatio = 3.0 / 4.0;
    private FrameLayout mFrame;

    public PreviewFrame(Context context) {
        this(context, null);
    }

    public PreviewFrame(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mFrame = (FrameLayout) findViewById(R.id.frame);

        if (mFrame == null) {
            throw new IllegalStateException(
                    "must provide child with id as \"frame\"");
        }
    }

    public void setAspectRatio(double ratio) {
        if (ratio <= 0.0) throw new IllegalArgumentException();

        if (mAspectRatio == ratio) {
            return;
        }

        mAspectRatio = ratio;
        requestLayout();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int frameWidth = getWidth();
        int frameHeight = getHeight();

        FrameLayout f = mFrame;

        int horizontalPadding = f.getPaddingLeft() + f.getPaddingRight();
        int verticalPadding = f.getPaddingBottom() + f.getPaddingTop();

        int previewWidth = frameWidth - horizontalPadding;
        int previewHeight = frameHeight - verticalPadding;

        if (previewWidth > previewHeight * mAspectRatio) {
            previewWidth = (int) (previewHeight * mAspectRatio + 0.5);
        } else {
            previewHeight = (int) (previewWidth / mAspectRatio + 0.5);
        }

        frameWidth = previewWidth + horizontalPadding;
        frameHeight = previewHeight + verticalPadding;

        f.measure(MeasureSpec.makeMeasureSpec(frameWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(frameHeight, MeasureSpec.EXACTLY));

        int hSpace = ((right - left) - frameWidth) / 2;
        int vSpace = ((bottom - top) - frameHeight) / 2;

        f.layout(left + hSpace, top + vSpace, right - hSpace, bottom - hSpace);


    }
}
