package com.curious.donkey.activity;

import android.os.Bundle;
import android.support.v4.view.WindowCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.webkit.WebView;

import com.curious.donkey.R;
import com.curious.donkey.web.DummyChromeClient;
import com.curious.donkey.web.DummyWebViewClient;
import com.curious.support.logger.Log;

/**
 * Created by Administrator on 2016/3/30.
 */
public class ShowRemoteWebPageActivity extends BaseActivity  {
    final static String TAG = "ShowRemoteWebPage";
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        supportRequestWindowFeature(WindowCompat.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_web_page);
        getSupportActionBar().setHideOnContentScrollEnabled(true);

        mWebView = (WebView) findViewById(R.id.webView);

        mWebView.getSettings().setJavaScriptEnabled(true);
        Log.d(TAG, mWebView.getSettings().getUserAgentString());
        mWebView.setWebChromeClient(new DummyChromeClient().showProgressLikeBrowser());
        mWebView.setWebViewClient(new DummyWebViewClient());
        mWebView.loadUrl("http://www.baidu.com");
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {


        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return;
        }


        super.onBackPressed();
    }
}
