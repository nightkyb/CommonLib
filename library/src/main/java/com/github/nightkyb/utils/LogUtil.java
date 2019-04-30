package com.github.nightkyb.utils;

import android.util.Log;

import com.github.nightkyb.BuildConfig;

/**
 * Author: nightkyb
 */
public class LogUtil {
    private static final String TAG = "CommonLib";
    private static boolean isDebug = BuildConfig.DEBUG;

    private LogUtil() {
    }

    public static void init(boolean isDebug) {
        LogUtil.isDebug = isDebug;
    }

    public static void i(String msg) {
        if (isDebug) {
            Log.i(TAG, msg);
        }
    }

    public static void d(String msg) {
        if (isDebug) {
            Log.d(TAG, msg);
        }
    }

    public static void e(String msg) {
        if (isDebug) {
            Log.e(TAG, msg);
        }
    }
}
