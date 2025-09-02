package com.github.jsbxyyx.xbook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.jsbxyyx.xbook.common.Common;
import com.github.jsbxyyx.xbook.common.DataCallback;
import com.github.jsbxyyx.xbook.common.JsonUtil;
import com.github.jsbxyyx.xbook.common.LogUtil;
import com.github.jsbxyyx.xbook.common.UiUtils;
import com.github.jsbxyyx.xbook.data.BookDbHelper;
import com.github.jsbxyyx.xbook.data.BookNetHelper;
import com.github.jsbxyyx.xbook.data.bean.Book;
import com.github.jsbxyyx.xbook.data.bean.Comment;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * @author jsbxyyx
 * @since 1.0
 */
public class DetailActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private BookNetHelper bookNetHelper;
    private BookDbHelper bookDbHelper;

    private RecyclerView rv_detail_suggest;
    private ListBookAdapter listBookAdapter;

    private RecyclerView rv_detail_comments;
    private ListCommentAdapter listCommentAdapter;

    private String detailUrl;
    private String bid;
    private Book mBook;

    public DetailActivity() {
        bookNetHelper = new BookNetHelper();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_detail);

        bookDbHelper = BookDbHelper.getInstance();

        detailUrl = getIntent().getStringExtra("detailUrl");
        if (Common.isEmpty(detailUrl)) {
            UiUtils.showToast("书籍地址为空");
            return;
        }
        bid = getIntent().getStringExtra("bid");

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
                    Picasso.get().load(mBook.getCoverImage()).error(R.drawable.baseline_menu_book_24).into(iv_detail_img);
                    tv_detail_year.setText(mBook.getYear());
                    tv_detail_publish.setText(mBook.getPublisher());
                    tv_detail_language.setText(mBook.getLanguage());
                    tv_detail_isbn.setText(mBook.getIsbn());
                    tv_detail_file.setText(mBook.getFile());
                });
            }
        });

        rv_detail_suggest = findViewById(R.id.rv_detail_suggest);
        LinearLayoutManager suggestLayoutManager = new LinearLayoutManager(this);
        rv_detail_suggest.setLayoutManager(suggestLayoutManager);
        rv_detail_suggest.setHasFixedSize(true);

        listBookAdapter = new ListBookAdapter(this, null);
        listBookAdapter.setOnItemClickListener((book, position) -> {
            LogUtil.d(TAG, "rv_detail_suggest: setOnItemClickListener: %s", book);
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("detailUrl", book.getDetailUrl());
            intent.putExtra("bid", book.getBid());
            startActivity(intent);
        });
        rv_detail_suggest.setAdapter(listBookAdapter);

        bookNetHelper.detailSuggest(detailUrl, new DataCallback<List<Book>>() {
            @Override
            public void call(List<Book> list, Throwable err) {
                runOnUiThread(() -> {
                    if (err != null) {
                        UiUtils.showToast("获取推荐书籍失败:" + err.getMessage());
                        return;
                    }
                    LogUtil.d(TAG, "suggest size : %s", list.size());
                    listBookAdapter.getDataList().clear();
                    listBookAdapter.getDataList().addAll(list);
                    listBookAdapter.notifyDataSetChanged();
                });
            }
        });

        rv_detail_comments = findViewById(R.id.rv_detail_comments);
        LinearLayoutManager commentsLayoutManager = new LinearLayoutManager(this);
        rv_detail_comments.setLayoutManager(commentsLayoutManager);
        rv_detail_comments.setHasFixedSize(true);

        listCommentAdapter = new ListCommentAdapter(this, null);
        rv_detail_comments.setAdapter(listCommentAdapter);

        bookNetHelper.bookComments(bid, new DataCallback<List<Comment>>() {

            @Override
            public void call(List<Comment> list, Throwable err) {
                runOnUiThread(() -> {
                    if (err != null) {
                        UiUtils.showToast("获取推荐书籍评论失败:" + err.getMessage());
                        return;
                    }
                    LogUtil.d(TAG, "comments size : %s", list.size());
                    listCommentAdapter.getDataList().clear();
                    listCommentAdapter.getDataList().addAll(list);
                    listCommentAdapter.notifyDataSetChanged();
                });
            }
        });

        findViewById(R.id.btn_detail_download).setOnClickListener(v -> {
            if (mBook == null || Common.isEmpty(mBook.getDownloadUrl())) {
                UiUtils.showToast("下载地址为空，请登录");
                return;
            }
            Book dbBook = bookDbHelper.findBookByBid(mBook.getBid());
            if (dbBook != null) {
                UiUtils.showToast("阅读列表存在《" + mBook.getTitle() + "》");
                return;
            }
            UiUtils.showToast("开始下载...");
            Intent intent = new Intent(this, DownloadBookService.class);
            intent.setAction(DownloadBookService.ACTION_START_DOWNLOAD);
            intent.putExtra(DownloadBookService.EXTRA_URL, mBook.getDownloadUrl());
            intent.putExtra(DownloadBookService.EXTRA_DIR, Common.xbook_dir);
            intent.putExtra(DownloadBookService.EXTRA_UID, mBook.getBid());
            intent.putExtra(DownloadBookService.EXTRA_TYPE, Common.not_downloaded);
            intent.putExtra(DownloadBookService.EXTRA_BOOK, JsonUtil.toJson(mBook));
            startService(intent);
        });
    }
}