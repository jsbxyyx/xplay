package com.github.jsbxyyx.xbook.common;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class UiUtils {

    private static Context mContext;

    public static void initContext(Context context) {
        mContext = context;
    }

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

    public static String getVersionName() {
        String versionName = "0";
        try {
            versionName = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException ex) {
        }
        return versionName;
    }

    public static void showToast(CharSequence text) {
        Toast.makeText(mContext, text, Toast.LENGTH_LONG).show();
    }

}
