package com.github.jsbxyyx.xbook.common;

import android.content.Context;

public class AppUtils {

    private static Context mContext;

    public static void initContext(Context context) {
        mContext = context;
    }

    public static Context getContext() {
        return mContext;
    }
}
