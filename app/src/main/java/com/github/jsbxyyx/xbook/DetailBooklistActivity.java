package com.github.jsbxyyx.xbook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.jsbxyyx.xbook.common.Common;
import com.github.jsbxyyx.xbook.common.DataCallback;
import com.github.jsbxyyx.xbook.common.JsonUtil;
import com.github.jsbxyyx.xbook.common.LogUtil;
import com.github.jsbxyyx.xbook.common.UiUtils;
import com.github.jsbxyyx.xbook.data.BookNetHelper;
import com.github.jsbxyyx.xbook.data.bean.Book;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

public class DetailBooklistActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private String booklistId;
    private RecyclerView rv_list;
    private ListBookAdapter listBookAdapter;

    private BookNetHelper bookNetHelper;

    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_booklist);

        bookNetHelper = new BookNetHelper();

        booklistId = getIntent().getStringExtra("booklist_id");
        if (Common.isBlank(booklistId)) {
            UiUtils.showToast("书籍集合ID无效");
            return;
        }
        String title = getIntent().getStringExtra("title");
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setTitle(title);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        rv_list = findViewById(R.id.rv_list);
        LinearLayoutManager suggestLayoutManager = new LinearLayoutManager(this);
        rv_list.setLayoutManager(suggestLayoutManager);
        rv_list.setHasFixedSize(true);

        listBookAdapter = new ListBookAdapter(this, null);
        listBookAdapter.setOnItemClickListener((book, position) -> {
            LogUtil.d(TAG, "rv_list: setOnItemClickListener: %s", book);
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("detailUrl", book.getDetailUrl());
            intent.putExtra("bid", book.getBid());
            startActivity(intent);
        }).setOnSubItemClickListener((book, type, position) -> {
            if (
                    Book.content_type_booklist.equals(book.getContent_type()) &&
                            Book.publisher_key.equals(type)
            ) {
                String extra = book.getExtra();
                if (!Common.isBlank(extra)) {
                    try {
                        JsonNode tree = JsonUtil.readTree(extra);
                        String booklist_id = tree.get("booklist_id").asText();
                        Intent intent = new Intent(this, DetailBooklistActivity.class);
                        intent.putExtra("booklist_id", booklist_id);
                        startActivity(intent);
                    } catch (Exception e) {
                        UiUtils.showToast("书籍集合参数错误");
                    }
                }
            }
        });
        rv_list.setAdapter(listBookAdapter);

        Button btn_more = findViewById(R.id.btn_more);
        btn_more.setOnClickListener((v) -> {
            showListView();
        });

        showListView();
    }

    private void showListView() {
        Button btn_more = findViewById(R.id.btn_more);

        DialogLoading loading = new DialogLoading(this);
        UiUtils.post(() -> {
            loading.show();
        });
        bookNetHelper.booklistDetail(booklistId, page, new DataCallback<List<Book>>() {
            @Override
            public void call(List<Book> list, Throwable err) {
                UiUtils.post(() -> {
                    loading.dismiss();
                    if (err != null) {
                        UiUtils.showToast("书籍集合查询失败: " + err.getMessage());
                        return;
                    }
                    if (page == 1 && list.isEmpty()) {
                        UiUtils.showToast("未查询到书籍集合");
                        btn_more.setVisibility(View.GONE);
                        return;
                    }
                    listBookAdapter.getDataList().addAll(list);
                    listBookAdapter.notifyDataSetChanged();
                    if (!list.isEmpty()) {
                        btn_more.setVisibility(View.VISIBLE);
                    } else {
                        btn_more.setVisibility(View.GONE);
                    }
                    page += 1;
                });
            }
        });
    }
}