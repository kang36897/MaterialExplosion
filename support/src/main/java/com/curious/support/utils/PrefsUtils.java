package com.curious.support.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2015/11/13.
 */
public class PrefsUtils {

    private final static String PREFERENCE_SENSITIVE_ACCOUNT = "pref_sensitive_account";
    private static PrefsUtils mSelf;


    private SharedPreferences mPrefs;


    private PrefsUtils(Context ctx) {
        mPrefs = ctx.getSharedPreferences(ctx.getPackageName() + PREFERENCE_SENSITIVE_ACCOUNT, Context.MODE_PRIVATE);
    }

    public synchronized static void init(Context ctx) {
        if (mSelf != null) {
            return;
        }

        mSelf = new PrefsUtils(ctx.getApplicationContext());
    }

    public static PrefsUtils getInstance() {
        return mSelf;
    }


    public void writePref(String key, String value) {
        mPrefs.edit().putString(key, value).apply();
    }

    public void writePref(String key, boolean value) {
        mPrefs.edit().putBoolean(key, value).apply();
    }

    public boolean readPref(String key, boolean defaultValue) {
        return mPrefs.getBoolean(key, defaultValue);
    }

    public String readPref(String key, String defaultValue) {
        return mPrefs.getString(key, defaultValue);
    }


    public int readPref(String key, int defaultValue) {
        return mPrefs.getInt(key, defaultValue);
    }

    public long readPref(String key, long defaultValue) {
        return mPrefs.getLong(key, defaultValue);
    }

    public void writePref(String key, int value) {

        mPrefs.edit().putInt(key, value).apply();
    }

    public void writePref(String key, long value) {
        mPrefs.edit().putLong(key, value).apply();
    }


}
