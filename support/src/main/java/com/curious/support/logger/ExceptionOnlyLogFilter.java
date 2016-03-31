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

        if (tr == null) {
            return;
        }

        if (mNext != null) {
            mNext.println(Log.NONE, tag, msg, null);
        }
    }

    @Override
    public void setNext(LogNode next) {
        mNext = next;
    }
}
