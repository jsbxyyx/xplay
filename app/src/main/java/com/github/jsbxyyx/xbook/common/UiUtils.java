package com.github.jsbxyyx.xbook.common;

import android.app.Activity;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class UiUtils {

    public static int getNavigationBarRealHeight(Activity activity) {
        Window window = activity.getWindow();
        View decorView = window.getDecorView();
        Rect rect = new Rect();
        decorView.getWindowVisibleDisplayFrame(rect);
        DisplayMetrics outMetrics = new DisplayMetrics();
        WindowManager windowManager = window.getWindowManager();
        windowManager.getDefaultDisplay().getRealMetrics(outMetrics);
        return outMetrics.heightPixels - rect.bottom;
    }

}
