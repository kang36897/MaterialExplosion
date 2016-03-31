package com.curious.donkey.web;

import android.app.Activity;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.ConsoleMessage.MessageLevel;

import com.curious.support.logger.Log;

/**
 * Created by Administrator on 2016/3/30.
 */
public class DummyChromeClient extends WebChromeClient {

    private String TAG = "DummyChromeClient";
    private boolean isShowProgressLikeBrowser = false;


    public DummyChromeClient showProgressLikeBrowser() {
        isShowProgressLikeBrowser = true;
        return this;
    }


    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        if (isShowProgressLikeBrowser) {
            // Activities and WebViews measure progress with different scales.
            // The progress meter will automatically disappear when we reach 100%
            ((Activity) view.getContext()).setProgress(newProgress * 100);
            return;
        }

        super.onProgressChanged(view, newProgress);
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage cm) {

        StringBuilder sb = new StringBuilder();
        sb.append(cm.message())
                .append("for line number: ")
                .append(cm.lineNumber())
                .append(" of ").append(cm.sourceId());

        if (cm.messageLevel().equals(MessageLevel.ERROR)) {
            Log.e(TAG, sb.toString());
        } else {
            Log.d(TAG, sb.toString());
        }


        return true;
    }
}
