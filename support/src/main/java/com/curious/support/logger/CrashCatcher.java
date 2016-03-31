package com.curious.support.logger;

/**
 * Created by Administrator on 2016/3/24.
 */
public class CrashCatcher implements Thread.UncaughtExceptionHandler {

    final static String TAG = "CrashCatcher";

    private Thread.UncaughtExceptionHandler mDefaultHandler;

    public void wrap(Thread.UncaughtExceptionHandler handler) {
        mDefaultHandler = handler;
    }


    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Log.e(TAG, " THREAD - " + thread.getName(), ex);


        if (mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        }
    }
}
