package com.curious.donkey.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import com.curious.donkey.R;
import com.curious.donkey.web.DummyChromeClient;
import com.curious.donkey.web.DummyWebViewClient;

/**
 * Created by Administrator on 2016/3/30.
 */
public class ShowLocalWebPageActivity extends AppCompatActivity {

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_local_web_page);

        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.setWebViewClient(new DummyWebViewClient());
        mWebView.setWebChromeClient(new DummyChromeClient());


        mWebView.loadUrl("file:///android_asset/Pointillism/index.html");
    }
}
