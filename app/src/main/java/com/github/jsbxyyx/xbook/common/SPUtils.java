package com.github.jsbxyyx.xbook.common;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author jsbxyyx
 */
public class SPUtils {

    public static final String FILE_NAME = "share_data";

    private static SharedPreferences sp;

    public static void init(Context context) {
        sp = context.getApplicationContext().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    public static void putData(String key, String data) {
        sp.edit().putString(key, data).apply();
    }

    public static String getData(String key) {
        return getData(key, "");
    }

    public static String getData(String key, String defValue) {
        return sp.getString(key, defValue);
    }

}
