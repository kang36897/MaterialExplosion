package com.curious.support.logger;

/**
 * Created by Administrator on 2016/3/25.
 */
public class LogChainBuilder {

    private LogNode mHead;
    private LogNode mCurrent;

    public LogChainBuilder() {
        mHead = mCurrent = new LogWrapper();
    }

    public LogChainBuilder(LogNode head) {
        mHead = head;
        mCurrent = head;
    }


    public LogChainBuilder append(LogNode node) {
        if (mHead == null) {
            mHead = node;
            mCurrent = node;
            return this;
        }

        mCurrent.setNext(node);
        mCurrent = node;

        return this;

    }


    public LogNode build() {
        return mHead;
    }


}
