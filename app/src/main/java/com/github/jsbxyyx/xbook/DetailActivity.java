package com.github.jsbxyyx.xbook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.jsbxyyx.xbook.common.Common;
import com.github.jsbxyyx.xbook.common.DataCallback;
import com.github.jsbxyyx.xbook.common.IdUtil;
import com.github.jsbxyyx.xbook.common.JsonUtil;
import com.github.jsbxyyx.xbook.common.LogUtil;
import com.github.jsbxyyx.xbook.common.ProgressListener;
import com.github.jsbxyyx.xbook.common.SPUtils;
import com.github.jsbxyyx.xbook.common.UiUtils;
import com.github.jsbxyyx.xbook.data.BookDbHelper;
import com.github.jsbxyyx.xbook.data.BookNetHelper;
import com.github.jsbxyyx.xbook.data.bean.Book;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jsbxyyx
 * @since 1.0
 */
public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "xbook";
    private BookNetHelper bookNetHelper;
    private BookDbHelper bookDbHelper;

    private ListView lv_detail_suggest;
    private ListBookAdapter lvListAdapter;

    private String detailUrl;
    private Book mBook;

    public DetailActivity() {
        bookNetHelper = new BookNetHelper();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        bookDbHelper = new BookDbHelper(this);

        detailUrl = getIntent().getStringExtra("detailUrl");
        if (Common.isEmpty(detailUrl)) {
            UiUtils.showToast("书籍地址为空");
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

        LoadingDialog loading = new LoadingDialog(this);
        loading.show();
        bookNetHelper.detail(detailUrl, new DataCallback<Book>() {
            @Override
            public void call(Book book, Throwable err) {
                if (err != null) {
                    runOnUiThread(() -> {
                        UiUtils.showToast("获取书籍详情失败:" + err.getMessage());
                    });
                    return;
                }
                loading.dismiss();
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

        lv_detail_suggest = findViewById(R.id.lv_detail_suggest);
        lvListAdapter = new ListBookAdapter(this, null);
        lv_detail_suggest.setAdapter(lvListAdapter);
        lv_detail_suggest.setOnItemClickListener((parent, view1, position, id) -> {
            Book t = (Book) lv_detail_suggest.getAdapter().getItem(position);
            LogUtil.d(TAG, "lv_detail_suggest: setOnItemClickListener: %s", t);
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("detailUrl", t.getDetailUrl());
            startActivity(intent);
        });
        bookNetHelper.detailSuggest(detailUrl, new DataCallback<List<Book>>() {
            @Override
            public void call(List<Book> list, Throwable err) {
                runOnUiThread(() -> {
                    if (err != null) {
                        UiUtils.showToast("获取推荐书籍失败:" + err.getMessage());
                        return;
                    }
                    LogUtil.d(TAG, "suggest size : %s", list.size());
                    lvListAdapter.getDataList().clear();
                    lvListAdapter.getDataList().addAll(list);
                    lvListAdapter.notifyDataSetChanged();
                    UiUtils.setListViewHeightBasedOnChildren(lv_detail_suggest);
                });
            }
        });

        findViewById(R.id.btn_detail_download).setOnClickListener(v -> {
            if (mBook == null || Common.isEmpty(mBook.getDownloadUrl())) {
                UiUtils.showToast("下载地址为空，请登录");
                return;
            }
            tv_download_progress.setVisibility(View.VISIBLE);
            tv_download_progress.setText("开始下载...");
            bookNetHelper.downloadWithMagic(mBook.getDownloadUrl(), Common.xbook_dir, mBook.getBid(), new DataCallback<File>() {
                @Override
                public void call(File file, Throwable err) {
                    if (err != null) {
                        runOnUiThread(() -> {
                            UiUtils.showToast("书籍下载失败:" + err.getMessage());
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
                        if (Common.checked.equals(sync_data)) {
                            bookNetHelper.cloudSync(bookDbHelper.findBookByBid(mBook.getBid()), new DataCallback<JsonNode>() {
                                @Override
                                public void call(JsonNode o, Throwable err) {
                                    if (err != null) {
                                        runOnUiThread(() -> {
                                            UiUtils.showToast("同步失败:" + err.getMessage());
                                        });
                                        return;
                                    }

                                    String sha = o.get("data").get("sha").asText();
                                    Book book_db = bookDbHelper.findBookById(mBook.getId() + "");
                                    if (book_db != null) {
                                        book_db.putRemarkProperty("sha", sha);
                                        bookDbHelper.updateBook(book_db);
                                    }

                                    runOnUiThread(() -> {
                                        if (book_db != null) {
                                            UiUtils.showToast("同步成功");
                                        }
                                    });
                                }
                            });
                        }
                    }
                    runOnUiThread(() -> {
                        UiUtils.showToast("下载成功");
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
            }, Common.MAGIC);
        });
    }
}