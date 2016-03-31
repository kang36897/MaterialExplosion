package com.curious.support.task;

import android.app.Activity;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2016/3/16.
 */
public abstract class AsyncTaskAdapter<T extends Activity> extends AsyncTask<String, Void, Object> {
    private WeakReference<Activity> mRef;
    private int mAction;

    public AsyncTaskAdapter(int action, Activity a) {
        if (!OnPostListener.class.isInstance(a)) {
            throw new IllegalArgumentException(a.getClass().getName() + " not implements OnPostListener interface");
        }

        mRef = new WeakReference<Activity>(a);
        mAction = action;
    }


    @Override
    protected void onPostExecute(Object o) {
        Activity a = mRef.get();
        if (a == null) {
            return;
        }

        ((OnPostListener) a).onPostExecute(mAction, o);

    }


    public interface OnPostListener {
        void onPostExecute(int action, Object e);
    }
}
