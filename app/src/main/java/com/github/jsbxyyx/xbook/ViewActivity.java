package com.github.jsbxyyx.xbook;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.jsbxyyx.xbook.common.Common;
import com.github.jsbxyyx.xbook.common.DataCallback;
import com.github.jsbxyyx.xbook.common.LogUtil;
import com.github.jsbxyyx.xbook.data.Book;
import com.github.jsbxyyx.xbook.data.BookDbHelper;
import com.github.jsbxyyx.xbook.data.BookNetHelper;
import com.github.jsbxyyx.xbook.httpserver.HttpServer;
import com.github.jsbxyyx.xbook.httpserver.MediaTypeFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

/**
 * @author jsbxyyx
 * @since 1.0
 */
public class ViewActivity extends AppCompatActivity {

    private HttpServer mHttpd;

    private String bookId;

    private BookDbHelper bookDbHelper;
    private BookNetHelper bookNetHelper;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        LogUtil.d(getClass().getSimpleName(), "onCreate");

        bookDbHelper = new BookDbHelper(this);
        bookNetHelper = new BookNetHelper();

        Intent intent = getIntent();
        String file_path = intent.getStringExtra("file_path");
        bookId = intent.getStringExtra("book_id");
        String cur = intent.getStringExtra("cur");
        String pages = intent.getStringExtra("pages");

        File f = new File(Common.xbook_dir);
        if (!f.exists()) {
            f.mkdirs();
        }

        int port = 5200;
        mHttpd = new HttpServer(port, new MediaTypeFactory(getBaseContext()));
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

            WebView wv_view = findViewById(R.id.wv_view);
            wv_view.getSettings().setJavaScriptEnabled(true);
            wv_view.addJavascriptInterface(new BookJavascript(this), "xbook");
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
                html = "pdf.html";
            }
            if (TextUtils.isEmpty(html)) {
                Toast.makeText(getBaseContext(), "不支持的文件格式:" + extension, Toast.LENGTH_LONG).show();
                return;
            }
            String url = String.format("http://127.0.0.1:%s/%s/%s?cur=%s&pages=%s&book_id=%s&name=%s",
                    port, www, html, cur, pages, bookId, name);
            wv_view.loadUrl(url);
        } catch (IOException e) {
            LogUtil.e(getClass().getSimpleName(), "onCreate: %s", LogUtil.getStackTraceString(e));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(getClass().getSimpleName(), "onDestroy");
        mHttpd.stop();

        Book book = bookDbHelper.findBookById(bookId);
        bookNetHelper.cloudSyncMeta(book, new DataCallback<JsonNode>() {
            @Override
            public void call(JsonNode o, Throwable err) {
                LogUtil.d(getClass().getSimpleName(), "view sync meta: %s : %s", book.getId(), book.getTitle());
                String sha = o.get("data").get("sha").asText();
                book.putRemarkProperty("sha", sha);
                bookDbHelper.updateBook(book);
            }
        });
    }

}