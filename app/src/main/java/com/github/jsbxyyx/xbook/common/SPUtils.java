package com.github.jsbxyyx.xbook.common;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author jsbxyyx
 */
public class SPUtils {

    public static final String FILE_NAME = "share_data";

    public static void putData(Context context, String key, String data) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, data);
        editor.apply();
    }

    public static String getData(Context context, String key) {
        return getData(context, key, "");
    }

    public static String getData(Context context, String key, String defValue) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        String data = sp.getString(key, defValue);
        return data;
    }

}
