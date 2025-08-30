package com.github.jsbxyyx.xbook;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
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

public class GeckoWebViewActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
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
            LogUtil.e(TAG, "onCreate: %s", LogUtil.getStackTraceString(e));
            UiUtils.showToast("服务启动失败");
        }

        String orientation = getIntent().getStringExtra("orientation");
        if ("v".equals(orientation)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if ("h".equals(orientation)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        WebProgress webProgress = findViewById(R.id.progress);
        webProgress.setColor("#1AAD19");

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

            session.setProgressDelegate(new GeckoSession.ProgressDelegate() {
                @Override
                public void onPageStart(@NonNull GeckoSession session, @NonNull String url) {
                    GeckoSession.ProgressDelegate.super.onPageStart(session, url);
                }

                @Override
                public void onPageStop(@NonNull GeckoSession session, boolean success) {
                    GeckoSession.ProgressDelegate.super.onPageStop(session, success);
                }

                @Override
                public void onProgressChange(@NonNull GeckoSession session, int progress) {
                    GeckoSession.ProgressDelegate.super.onProgressChange(session, progress);
                    webProgress.setProgress(progress);
                }

                @Override
                public void onSecurityChange(@NonNull GeckoSession session, @NonNull SecurityInformation securityInfo) {
                    GeckoSession.ProgressDelegate.super.onSecurityChange(session, securityInfo);
                }

                @Override
                public void onSessionStateChange(@NonNull GeckoSession session, @NonNull GeckoSession.SessionState sessionState) {
                    GeckoSession.ProgressDelegate.super.onSessionStateChange(session, sessionState);
                }
            });

            session.open(runtime);
            webView.setSession(session);

            webView.getSession().getUserAgent().accept((a) -> {
                LogUtil.d(TAG, "ua: %s", a);
            });

            session.loadUri(url);
            webProgress.show();
        } catch (Exception e) {
            LogUtil.e(TAG, "onCreate: %s", LogUtil.getStackTraceString(e));
            UiUtils.showToast("网页打开失败");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "onDestroy");
        if (mHttpd != null) {
            mHttpd.stop();
        }
    }
}