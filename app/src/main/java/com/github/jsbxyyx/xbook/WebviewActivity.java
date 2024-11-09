package com.github.jsbxyyx.xbook;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.jsbxyyx.xbook.common.Common;
import com.github.jsbxyyx.xbook.common.LogUtil;
import com.github.jsbxyyx.xbook.common.UiUtils;
import com.github.jsbxyyx.xbook.httpserver.BizHttpServer;

import org.mozilla.geckoview.GeckoRuntime;
import org.mozilla.geckoview.GeckoRuntimeSettings;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoSessionSettings;
import org.mozilla.geckoview.GeckoView;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;

public class WebviewActivity extends AppCompatActivity {

    private GeckoView webView;
    private String url;
    private static GeckoRuntime runtime;

    private NanoHTTPD mHttpd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        url = getIntent().getStringExtra("url");

        if (Common.isBlank(url)) {
            UiUtils.showToast("没有地址");
            return;
        }

        mHttpd = new BizHttpServer(5201, this);
        try {
            mHttpd.start();
        } catch (IOException e) {
            LogUtil.e(getClass().getSimpleName(), "onCreate: %s", LogUtil.getStackTraceString(e));
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
                GeckoRuntimeSettings.Builder builder = new GeckoRuntimeSettings.Builder()
                        .allowInsecureConnections(GeckoRuntimeSettings.ALLOW_ALL)
                        .javaScriptEnabled(true)
                        .doubleTapZoomingEnabled(true)
                        .inputAutoZoomEnabled(true)
                        .forceUserScalableEnabled(true)
                        .aboutConfigEnabled(true)
                        .loginAutofillEnabled(true)
                        .webManifest(true)
                        .consoleOutput(true)
                        .remoteDebuggingEnabled(BuildConfig.DEBUG)
                        .debugLogging(BuildConfig.DEBUG);
                runtime = GeckoRuntime.create(this, builder.build());
            }
            GeckoSessionSettings settings = session.getSettings();
            settings.setAllowJavascript(true);
            settings.setUserAgentMode(GeckoSessionSettings.USER_AGENT_MODE_MOBILE);

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
        if (mHttpd != null) {
            mHttpd.stop();
        }
    }
}