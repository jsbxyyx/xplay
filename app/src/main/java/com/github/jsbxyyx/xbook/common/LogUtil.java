package com.github.jsbxyyx.xbook.common;

import android.util.Log;

import com.github.jsbxyyx.xbook.BuildConfig;

import java.util.Arrays;
import java.util.IllegalFormatException;

/**
 * @author jsbxyyx
 */
public class LogUtil {

    private static volatile boolean enable = true;

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
                Log.v(tag, formatMessage(format, args));
            } else {
                System.out.println(tag + " " + formatMessage(format, args));
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
                Log.d(tag, formatMessage(format, args));
            } else {
                System.out.println(tag + " " + formatMessage(format, args));
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
            Log.i(tag, formatMessage(format, args));
        } else {
            System.out.println(tag + " " + formatMessage(format, args));
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
            Log.w(tag,formatMessage(format, args));
        } else {
            System.out.println(tag + " " + formatMessage(format, args));
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
            Log.e(tag, formatMessage(format, args));
        } else {
            System.out.println(tag + " " + formatMessage(format, args));
        }
    }

    public static String getStackTraceString(Throwable tr) {
        return Log.getStackTraceString(tr);
    }

    public static boolean isEmpty(Object[] args) {
        return args == null || args.length == 0;
    }

    private static String formatMessage(String format, Object... args) {
        if (format == null) {
            return "";
        }
        if (isEmpty(args)) {
            return format;
        }
        try {
            return String.format(format, args);
        } catch (IllegalFormatException e) {
            String errorMsg = "[LogUtil] Format error: " + getStackTraceString(e);
            if (enable) {
                Log.w("LogUtil", errorMsg);
            } else {
                System.err.println("LogUtil " + errorMsg);
            }
        }
        return format + " " + Arrays.toString(args);
    }

}
