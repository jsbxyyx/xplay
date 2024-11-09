package com.github.jsbxyyx.xbook;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.jsbxyyx.xbook.common.Common;
import com.github.jsbxyyx.xbook.common.DataCallback;
import com.github.jsbxyyx.xbook.common.IdUtil;
import com.github.jsbxyyx.xbook.common.JsonUtil;
import com.github.jsbxyyx.xbook.common.LogUtil;
import com.github.jsbxyyx.xbook.common.SPUtils;
import com.github.jsbxyyx.xbook.common.SessionManager;
import com.github.jsbxyyx.xbook.common.UiUtils;
import com.github.jsbxyyx.xbook.data.BookDbHelper;
import com.github.jsbxyyx.xbook.data.BookNetHelper;
import com.github.jsbxyyx.xbook.data.bean.Book;
import com.github.jsbxyyx.xbook.data.bean.ViewTime;
import com.github.jsbxyyx.xbook.httpserver.FileHttpServer;
import com.github.jsbxyyx.xbook.httpserver.MediaTypeFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jsbxyyx
 * @since 1.0
 */
public class ViewActivity extends AppCompatActivity {

    private FileHttpServer mHttpd;
    private WebView webView;
    private String bookId;
    private String bookTitle;

    private BookDbHelper bookDbHelper;
    private BookNetHelper bookNetHelper;

    private long startTime;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        LogUtil.d(getClass().getSimpleName(), "onCreate");

        bookDbHelper = new BookDbHelper(this);
        bookNetHelper = new BookNetHelper();

        int navH = UiUtils.getNavigationBarRealHeight(this);

        Intent intent = getIntent();
        String file_path = intent.getStringExtra("file_path");
        bookId = intent.getStringExtra("book_id");
        bookTitle = intent.getStringExtra("book_title");
        String cur = intent.getStringExtra("cur");
        String pages = intent.getStringExtra("pages");

        File f = new File(Common.xbook_dir);
        if (!f.exists()) {
            f.mkdirs();
        }

        if (!new File(file_path).exists()) {
            UiUtils.showToast("书籍不存在，请重新下载");
            return;
        }

        int port = 5200;
        mHttpd = new FileHttpServer(port, new MediaTypeFactory(getBaseContext()));
        try {
            mHttpd.start();
        } catch (IOException e) {
            LogUtil.e(getClass().getSimpleName(), "onCreate: %s", LogUtil.getStackTraceString(e));
        }

        try {
            String www = "www";
            String[] wwws = getResources().getAssets().list(www);
            File wwwDir = new File(Common.xbook_dir + "/" + www);
            if (!wwwDir.exists()) {
                wwwDir.mkdirs();
            }
            for (String file : wwws) {
                try (InputStream in = getResources().getAssets().open(www + "/" + file);
                     FileOutputStream out = new FileOutputStream(wwwDir + "/" + file)) {
                    Common.copy(in, out);
                }
            }

            webView = findViewById(R.id.wv_view);
            webView.getSettings().setJavaScriptEnabled(true);
            // webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            // webView.clearCache(true);
            webView.addJavascriptInterface(new BookJavascript(this), "xbook");
            String name = Common.urlEncode(
                    Common.urlEncode(
                            file_path.replace(Common.xbook_dir + "/", "")
                    )
            );
            String extension = MediaTypeFactory.getFilenameExtension(file_path);
            String html = "";
            if ("epub".equalsIgnoreCase(extension)) {
                html = "epub.html";
            } else if ("pdf".equalsIgnoreCase(extension)) {
                webView.getSettings().setUseWideViewPort(true);
                webView.getSettings().setLoadWithOverviewMode(true);
                webView.getSettings().setSupportZoom(true);
                webView.getSettings().setBuiltInZoomControls(true);
                webView.getSettings().setDisplayZoomControls(false);
                webView.setInitialScale(10);
                html = "pdf.html";
            }
            if (Common.isEmpty(html)) {
                UiUtils.showToast("不支持的文件格式:" + extension);
                return;
            }
            String url = String.format(
                    "http://127.0.0.1:%s/%s/%s?%s",
                    port, www, html,
                    String.format("cur=%s&pages=%s&book_id=%s&name=%s&t=%s&navh=%s",
                            cur, pages, bookId, name, System.currentTimeMillis(), navH)
            );
            webView.loadUrl(url);
        } catch (IOException e) {
            LogUtil.e(getClass().getSimpleName(), "onCreate: %s", LogUtil.getStackTraceString(e));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTime = SystemClock.elapsedRealtime();
        LogUtil.d(getClass().getSimpleName(), "startTime:%s", startTime);
    }

    @Override
    protected void onPause() {
        super.onPause();
        long endTime = SystemClock.elapsedRealtime();
        long stayTime = endTime - startTime;
        LogUtil.d(getClass().getSimpleName(), "endTime:%s - startTime:%s = stayTime:%s", endTime, startTime, stayTime);

        ViewTime viewTime = new ViewTime();
        viewTime.setId(IdUtil.nextId());
        viewTime.setTargetId(bookId);
        viewTime.setTargetType("1");
        viewTime.setTime(stayTime);
        Map<String, String> kv = Common.parseKv(SessionManager.getSession());
        viewTime.setUser(kv.getOrDefault(Common.serv_userid, ""));
        Map<String, String> remark = new HashMap<>();
        remark.put("bookTitle", bookTitle);
        viewTime.setRemark(JsonUtil.toJson(remark));
        bookDbHelper.insertViewTime(viewTime);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(getClass().getSimpleName(), "onDestroy");
        if (mHttpd != null) {
            mHttpd.stop();
        }
        if (webView != null) {
            webView.destroy();
        }

        String syncData = SPUtils.getData(getBaseContext(), Common.sync_key);
        if (Common.checked.equals(syncData)) {
            Book book = bookDbHelper.findBookById(bookId);
            if (book != null) {
                bookNetHelper.cloudSyncMeta(book, new DataCallback<JsonNode>() {
                    @Override
                    public void call(JsonNode o, Throwable err) {
                        if (err != null) {
                            LogUtil.e(getClass().getSimpleName(), "view sync meta err. %s", LogUtil.getStackTraceString(err));
                            runOnUiThread(() -> {
                                UiUtils.showToast("同步书籍失败:" + err.getMessage());
                            });
                            return;
                        }
                        LogUtil.d(getClass().getSimpleName(), "view sync meta: %s : %s", book.getId(), book.getTitle());
                        String sha = o.get("data").get("sha").asText();
                        book.putRemarkProperty("sha", sha);
                        bookDbHelper.updateBook(book);
                    }
                });
            }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
                webView.evaluateJavascript("javascript:handleVolumeKey('up')", null);
                return true;
            } else if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
                webView.evaluateJavascript("javascript:handleVolumeKey('down')", null);
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

}