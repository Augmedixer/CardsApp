package com.augmedix.cardsapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Utils {
    private Context mContext = null;
    private SharedPreferences mSharedPreferences = null;

    static Utils sInstance = null;

    public static Utils getInstance(Context context) {
        if (sInstance == null) sInstance = new Utils(context);
        return sInstance;
    }

    Utils(Context context) {
        mContext = context;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getStringPref(String name, String defaultValue) {
        return mSharedPreferences.getString(name, defaultValue);
    }

    public long getLong(String name, long defaultValue) {
        return mSharedPreferences.getLong(name, defaultValue);
    }

    public boolean getBoolean(String name, boolean defaultValue) {
        return mSharedPreferences.getBoolean(name, defaultValue);
    }

    public void set(String name, Object value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        if (value instanceof Boolean) editor.putBoolean(name, (Boolean) value);
        else if (value instanceof Long) editor.putLong(name, (Long) value);
        else if (value instanceof String) editor.putString(name, (String) value);
        editor.commit();
    }

    public void clearAllPrefs() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.clear();
        editor.commit();
    }
}
