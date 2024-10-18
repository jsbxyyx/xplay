package com.github.jsbxyyx.xbook.common;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * @author jsbxyyx
 */
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

    public static int getVersionCode() {
        int versionCode = 0;
        try {
            versionCode = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException ex) {
        }
        return versionCode;
    }

    public static void showToast(CharSequence text) {
        Toast.makeText(mContext, text, Toast.LENGTH_LONG).show();
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem instanceof ViewGroup) {
                listItem.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }

            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public static int dp2px(Context context, int i) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, i, context.getResources().getDisplayMetrics());
    }

    public static int dp2sp(Context context, int i) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, i, context.getResources().getDisplayMetrics());
    }

}
