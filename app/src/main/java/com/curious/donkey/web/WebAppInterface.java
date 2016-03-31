package com.curious.donkey.web;

import android.webkit.JavascriptInterface;

/**
 * Created by Administrator on 2016/3/30.
 */
public class WebAppInterface<T> {

    private T mHelper;

    public WebAppInterface(T t) {
        mHelper = t;
    }


    @JavascriptInterface
    public void onHandleJavascriptEvent() {


    }

}
