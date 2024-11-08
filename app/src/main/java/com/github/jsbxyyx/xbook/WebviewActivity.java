package com.github.jsbxyyx.xbook;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.jsbxyyx.xbook.common.Common;
import com.github.jsbxyyx.xbook.common.LogUtil;
import com.github.jsbxyyx.xbook.common.UiUtils;

import org.mozilla.geckoview.GeckoRuntime;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoView;

public class WebviewActivity extends AppCompatActivity {

    private GeckoView webView;
    private String url;
    private static GeckoRuntime runtime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        url = getIntent().getStringExtra("url");

        if (Common.isBlank(url)) {
            UiUtils.showToast("没有地址");
            return;
        }

        String orientation = getIntent().getStringExtra("orientation");
        if ("v".equals(orientation) &&
                getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if ("h".equals(orientation) &&
                getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        try {
            webView = findViewById(R.id.wv_html_view);
            GeckoSession session = new GeckoSession();
            if (runtime == null) {
                runtime = GeckoRuntime.create(this);
            }
            session.open(runtime);
            webView.setSession(session);

            webView.getSession().getUserAgent().accept((a) -> {
                LogUtil.d(getClass().getSimpleName(), "ua: %s", a);
            });

            session.loadUri(url);
        } catch (Exception e) {
            LogUtil.e(getClass().getSimpleName(), "onCreate: %s", LogUtil.getStackTraceString(e));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(getClass().getSimpleName(), "onDestroy");
    }
}