package com.curious.donkey.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.text.TextUtilsCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.curious.donkey.R;
import com.curious.support.logger.Log;

import java.io.File;

/**
 * Created by lulala on 10/4/16.
 */
public class ImageReviewFragment extends Fragment {

    public final static String SOURCE_IMG = "source_img";
    public final static String SOURCE_IMG_PATH = "source_img_path";
    private static final String TAG = "ImageReviewFragment";

    private ImageView mSourceView;
    private Button mDropBtn;
    private Button mChooseBtn;

    private Bitmap mImg;
    private String mPath;

    public static ImageReviewFragment getInstance(Bundle args) {
        ImageReviewFragment fragment = new ImageReviewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public interface OnImageActionListener {
        void onImageReceived(boolean kept, String path);
    }

    private OnImageActionListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (!(context instanceof OnImageActionListener)) {
            throw new IllegalArgumentException("you must implement OnImageActionListener interface first");
        }

        mListener = (OnImageActionListener) context;

        mImg = getArguments().getParcelable(SOURCE_IMG);
        mPath = getArguments().getString(SOURCE_IMG_PATH);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_image_review, container, false);

        mSourceView = (ImageView) contentView.findViewById(R.id.source_img);
        mDropBtn = (Button) contentView.findViewById(R.id.drop_this);
        mChooseBtn = (Button) contentView.findViewById(R.id.take_this);

        return contentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSourceView.setImageBitmap(mImg);
        mDropBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread("delete the image") {
                    @Override
                    public void run() {
                        if (TextUtils.isEmpty(mPath)) {
                            return;
                        }
                        try {
                            File file = new File(mPath);
                            file.delete();
                        } catch (Exception e) {
                            Log.e(TAG, "delete the image", e);
                        }
                    }
                }.start();

                mListener.onImageReceived(false, mPath);
            }
        });


        mChooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mListener.onImageReceived(true, mPath);
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mImg != null) {
            mImg.recycle();
            mImg = null;
        }
    }


}
