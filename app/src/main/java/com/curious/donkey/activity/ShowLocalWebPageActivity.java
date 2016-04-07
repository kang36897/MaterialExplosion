package com.curious.donkey.activity;

import android.annotation.SuppressLint;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.curious.donkey.R;
import com.curious.donkey.web.DummyChromeClient;
import com.curious.donkey.web.DummyJsInterface;
import com.curious.donkey.web.DummyWebViewClient;

/**
 * Created by Administrator on 2016/3/30.
 */
public class ShowLocalWebPageActivity extends AppCompatActivity {

    public final static boolean IS_NEWER_THAN_KITKAT = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    private WebView mWebView;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_local_web_page);

        mWebView = (WebView) findViewById(R.id.webView);
        if (IS_NEWER_THAN_KITKAT) {
            if (0 != (getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE)) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        }

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setAllowFileAccessFromFileURLs(true);
        mWebView.getSettings().setUseWideViewPort(true);
        if (IS_NEWER_THAN_KITKAT) {
            mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        }
        mWebView.setWebViewClient(new DummyWebViewClient());
        mWebView.setWebChromeClient(new DummyChromeClient());
        mWebView.addJavascriptInterface(new DummyJsInterface(this), "Android");

        mWebView.loadUrl("file:///android_asset/Pointillism/index.html");
    }
}
