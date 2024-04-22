package com.github.jsbxyyx.xbook.common;

import android.util.Log;

import com.github.jsbxyyx.xbook.BuildConfig;

/**
 * @author jsbxyyx
 * @since 1.0
 */
public class LogUtil {

    public static void v(String tag, String format, Object... args) {
        if (BuildConfig.DEBUG) {
            if (tag == null) {
                tag = "";
            }
            if (format == null) {
                format = "";
            }
            Log.v(tag, String.format(format, args));
        }
    }

    public static void d(String tag, String format, Object... args) {
        if (BuildConfig.DEBUG) {
            if (tag == null) {
                tag = "";
            }
            if (format == null) {
                format = "";
            }
            Log.d(tag, String.format(format, args));
        }
    }

    public static void i(String tag, String format, Object... args) {
        if (tag == null) {
            tag = "";
        }
        if (format == null) {
            format = "";
        }
        Log.i(tag, String.format(format, args));
    }

    public static void w(String tag, String format, Object... args) {
        if (tag == null) {
            tag = "";
        }
        if (format == null) {
            format = "";
        }
        Log.w(tag, String.format(format, args));
    }

    public static void e(String tag, String format, Object... args) {
        if (tag == null) {
            tag = "";
        }
        if (format == null) {
            format = "";
        }
        Log.e(tag, String.format(format, args));
    }

    public static String getStackTraceString(Throwable tr) {
        return Log.getStackTraceString(tr);
    }

}
