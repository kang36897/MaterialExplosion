package com.curious.support.logger;

/**
 * Created by Administrator on 2016/3/25.
 */
public class ExceptionOnlyLogFilter implements LogNode {

    private LogNode mNext;

    public LogNode getNext() {
        return mNext;
    }


    @Override
    public void println(int priority, String tag, String msg, Throwable tr) {

        if (tr == null && priority != Log.TREASURE) {
            return;
        }

        String useMsg = msg;
        if (useMsg == null) {
            useMsg = "";
        }

        if (tr != null) {
            useMsg += "\n" + android.util.Log.getStackTraceString(tr);
        }

        if (mNext != null) {
            mNext.println(Log.NONE, tag, useMsg, tr);
        }
    }

    @Override
    public void setNext(LogNode next) {
        mNext = next;
    }
}
