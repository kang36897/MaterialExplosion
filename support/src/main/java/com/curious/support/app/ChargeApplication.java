package com.curious.support.app;

import android.app.Application;

import com.curious.support.logger.CrashCatcher;
import com.curious.support.logger.ExceptionOnlyLogFilter;
import com.curious.support.logger.Log;
import com.curious.support.logger.LogChainBuilder;
import com.curious.support.logger.LogFile;


/**
 * Created by Administrator on 2016/3/25.
 */
public class ChargeApplication extends Application {

    static {
        Thread.UncaughtExceptionHandler handler = Thread.getDefaultUncaughtExceptionHandler();
        CrashCatcher crashCatcher = new CrashCatcher();
        crashCatcher.wrap(handler);
        Thread.setDefaultUncaughtExceptionHandler(crashCatcher);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        LogChainBuilder builder = new LogChainBuilder();
        builder.append(new ExceptionOnlyLogFilter()).append(new LogFile(this));
        Log.setLogNode(builder.build());

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
