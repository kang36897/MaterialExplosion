package com.curious.donkey.activity;

import android.os.Bundle;
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
public class ShowRemoteWebPageActivity extends AppCompatActivity {
    final static String TAG = "ShowRemoteWebPage";
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_show_web_page);

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
