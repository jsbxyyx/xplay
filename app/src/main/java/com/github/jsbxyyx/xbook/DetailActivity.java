package com.github.jsbxyyx.xbook;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.jsbxyyx.xbook.common.Common;
import com.github.jsbxyyx.xbook.common.DataCallback;
import com.github.jsbxyyx.xbook.common.IdUtil;
import com.github.jsbxyyx.xbook.common.JsonUtil;
import com.github.jsbxyyx.xbook.common.LogUtil;
import com.github.jsbxyyx.xbook.common.ProgressListener;
import com.github.jsbxyyx.xbook.common.SPUtils;
import com.github.jsbxyyx.xbook.data.bean.Book;
import com.github.jsbxyyx.xbook.data.BookDbHelper;
import com.github.jsbxyyx.xbook.data.BookNetHelper;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jsbxyyx
 * @since 1.0
 */
public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "xbook";
    private BookNetHelper bookNetHelper;
    private BookDbHelper bookDbHelper;

    private Book mBook;

    public DetailActivity() {
        bookNetHelper = new BookNetHelper();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        bookDbHelper = new BookDbHelper(this);

        String detailUrl = getIntent().getStringExtra("detailUrl");
        if (Common.isEmpty(detailUrl)) {
            Toast.makeText(getBaseContext(), "书籍地址为空", Toast.LENGTH_LONG).show();
            return;
        }

        TextView tv_detail_title = findViewById(R.id.tv_detail_title);
        ImageView iv_detail_img = findViewById(R.id.iv_detail_img);
        TextView tv_detail_year = findViewById(R.id.tv_detail_year);
        TextView tv_detail_publish = findViewById(R.id.tv_detail_publish);
        TextView tv_detail_language = findViewById(R.id.tv_detail_language);
        TextView tv_detail_isbn = findViewById(R.id.tv_detail_isbn);
        TextView tv_detail_file = findViewById(R.id.tv_detail_file);
        TextView tv_download_progress = findViewById(R.id.tv_download_progress);

        bookNetHelper.detail(detailUrl, new DataCallback<Book>() {
            @Override
            public void call(Book book, Throwable err) {
                LogUtil.d(TAG, "call: %s", book);
                mBook = book;
                mBook.setDetailUrl(detailUrl);
                runOnUiThread(() -> {
                    tv_detail_title.setText(mBook.getTitle());
                    Picasso.get().load(mBook.getCoverImage()).into(iv_detail_img);
                    tv_detail_year.setText(mBook.getYear());
                    tv_detail_publish.setText(mBook.getPublisher());
                    tv_detail_language.setText(mBook.getLanguage());
                    tv_detail_isbn.setText(mBook.getIsbn());
                    tv_detail_file.setText(mBook.getFile());
                });
            }
        });

        findViewById(R.id.btn_detail_download).setOnClickListener(v -> {
            String downloadUrl = mBook.getDownloadUrl();
            if (Common.isEmpty(downloadUrl)) {
                Toast.makeText(getBaseContext(), "下载地址为空，请登录", Toast.LENGTH_LONG).show();
                return;
            }
            bookNetHelper.download(downloadUrl, Common.xbook_dir, mBook.getBid(), new DataCallback<File>() {
                @Override
                public void call(File file, Throwable err) {
                    if (err != null) {
                        runOnUiThread(() -> {
                            Toast.makeText(getBaseContext(), "err:" + err.getMessage(), Toast.LENGTH_LONG).show();
                        });
                        return;
                    }
                    LogUtil.d(TAG, "call: file: %s : %s", file.getAbsolutePath(), file.length());
                    Map<String, Object> remark = new HashMap<>();
                    remark.put("file_path", file.getAbsolutePath());
                    mBook.setRemark(JsonUtil.toJson(remark));
                    mBook.setUser(SPUtils.getData(getBaseContext(), Common.profile_email_key));
                    Book by = bookDbHelper.findBookByBid(mBook.getBid());
                    if (by == null) {
                        mBook.setId(IdUtil.nextId());
                        bookDbHelper.insertBook(mBook);
                        String sync_data = SPUtils.getData(getBaseContext(), Common.sync_key);
                        if (Common.sync_key_checked.equals(sync_data)) {
                            bookNetHelper.cloudSync(bookDbHelper.findBookByBid(mBook.getBid()), new DataCallback<JsonNode>() {
                                @Override
                                public void call(JsonNode o, Throwable err) {
                                    runOnUiThread(() -> {
                                        if (err != null) {
                                            Toast.makeText(getBaseContext(), "同步失败", Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                        String sha = o.get("data").get("sha").asText();
                                        Book book_db = bookDbHelper.findBookById(mBook.getId() + "");
                                        if (book_db != null) {
                                            book_db.putRemarkProperty("sha", sha);
                                            bookDbHelper.updateBook(book_db);
                                            Toast.makeText(getBaseContext(), "同步成功", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            });
                        }
                    }
                    runOnUiThread(() -> {
                        Toast.makeText(getBaseContext(), "下载成功", Toast.LENGTH_LONG).show();
                    });
                }
            }, new ProgressListener() {
                @Override
                public void onProgress(long bytesRead, long contentLength) {
                    runOnUiThread(() -> {
                        tv_download_progress.setVisibility(View.VISIBLE);
                        tv_download_progress.setText(String.format("下载进度：%.1f%%", bytesRead * 1.0 / contentLength * 100));
                    });
                }
            });
        });
    }
}