package com.github.jsbxyyx.xbook;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.jsbxyyx.xbook.common.LogUtil;

import org.mozilla.geckoview.GeckoRuntime;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoView;

/**
 * @author jsbxyyx
 */
public class VideoViewActivity2 extends AppCompatActivity {

    private GeckoView webView;
    private String playUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);

        playUrl = getIntent().getStringExtra("playUrl");

        webView = findViewById(R.id.wv_video_view);
        GeckoSession session = new GeckoSession();
        GeckoRuntime runtime = GeckoRuntime.create(this);
        session.open(runtime);
        webView.setSession(session);

        try {
            LogUtil.d(getClass().getSimpleName(), "ua: %s", webView.getSession().getUserAgent().poll());
        } catch (Throwable e) {
            LogUtil.e(getClass().getSimpleName(), "%s", e);
        }

        session.loadUri(playUrl);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.destroyDrawingCache();
        }
    }
}