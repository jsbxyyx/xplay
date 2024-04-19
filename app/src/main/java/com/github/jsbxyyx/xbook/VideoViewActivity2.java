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
    private static GeckoRuntime runtime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);

        playUrl = getIntent().getStringExtra("playUrl");

        webView = findViewById(R.id.wv_video_view);
        GeckoSession session = new GeckoSession();
        if (runtime == null) {
            runtime = GeckoRuntime.create(this);
        }
        session.open(runtime);
        webView.setSession(session);

        webView.getSession().getUserAgent().accept((a) -> {
            LogUtil.d(getClass().getSimpleName(), "ua: %s", a);
        });

        session.loadUri(playUrl);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.releaseSession().close();
        }
    }
}