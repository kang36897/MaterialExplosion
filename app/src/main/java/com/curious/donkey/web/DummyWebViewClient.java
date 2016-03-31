package com.curious.donkey.web;

import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Administrator on 2016/3/30.
 */
public class DummyWebViewClient extends WebViewClient {



    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {

        if (isHandledBySelf(url)) {
            return false;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        view.getContext().startActivity(intent);

        return true;
    }

    protected boolean isHandledBySelf(String url) {
        return true;
    }





}
