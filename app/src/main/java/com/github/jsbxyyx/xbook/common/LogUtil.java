package com.github.jsbxyyx.xbook.common;

import android.util.Log;

import com.github.jsbxyyx.xbook.BuildConfig;

/**
 * @author jsbxyyx
 */
public class LogUtil {

    private static boolean enable = true;

    public static void setEnable(boolean enable_) {
        enable = enable_;
    }

    public static void v(String tag, String format, Object... args) {
        if (BuildConfig.DEBUG) {
            if (tag == null) {
                tag = "";
            }
            if (format == null) {
                format = "";
            }
            if (enable) {
                Log.v(tag, String.format(format, args));
            } else {
                System.out.println(tag + " " + String.format(format, args));
            }
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
            if (enable) {
                Log.d(tag, String.format(format, args));
            } else {
                System.out.println(tag + " " + String.format(format, args));
            }
        }
    }

    public static void i(String tag, String format, Object... args) {
        if (tag == null) {
            tag = "";
        }
        if (format == null) {
            format = "";
        }
        if (enable) {
            Log.i(tag, String.format(format, args));
        } else {
            System.out.println(tag + " " + String.format(format, args));
        }
    }

    public static void w(String tag, String format, Object... args) {
        if (tag == null) {
            tag = "";
        }
        if (format == null) {
            format = "";
        }
        if (enable) {
            Log.w(tag, String.format(format, args));
        } else {
            System.out.println(tag + " " + String.format(format, args));
        }
    }

    public static void e(String tag, String format, Object... args) {
        if (tag == null) {
            tag = "";
        }
        if (format == null) {
            format = "";
        }
        if (enable) {
            Log.e(tag, String.format(format, args));
        } else {
            System.out.println(tag + " " + String.format(format, args));
        }
    }

    public static String getStackTraceString(Throwable tr) {
        return Log.getStackTraceString(tr);
    }

}
