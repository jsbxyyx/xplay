package com.github.jsbxyyx.xbook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.jsbxyyx.xbook.common.Common;
import com.github.jsbxyyx.xbook.common.DataCallback;
import com.github.jsbxyyx.xbook.common.JsonUtil;
import com.github.jsbxyyx.xbook.common.LogUtil;
import com.github.jsbxyyx.xbook.common.SPUtils;
import com.github.jsbxyyx.xbook.common.UiUtils;
import com.github.jsbxyyx.xbook.data.BookNetHelper;
import com.github.jsbxyyx.xbook.data.bean.Book;

import java.util.List;

/**
 * @author jsbxyyx
 * @since 1.0
 */
public class ListFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();

    private View mView;
    private Activity mActivity;
    private Spinner sp_type;
    private RecyclerView rv_list;
    private ListBookAdapter listBookAdapter;
    private BookNetHelper bookNetHelper;

    private int page = 1;

    public ListFragment() {
        bookNetHelper = new BookNetHelper();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mView = view;
        mActivity = getActivity();

        sp_type = view.findViewById(R.id.sp_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                mActivity,
                R.array.types_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_type.setAdapter(adapter);
        sp_type.setSelection(0);

        rv_list = view.findViewById(R.id.rv_list);
        LinearLayoutManager suggestLayoutManager = new LinearLayoutManager(mActivity);
        rv_list.setLayoutManager(suggestLayoutManager);
        rv_list.setHasFixedSize(true);

        listBookAdapter = new ListBookAdapter(mActivity, null);
        listBookAdapter.setOnItemClickListener((book, position) -> {
            LogUtil.d(TAG, "rv_list: setOnItemClickListener: %s", book);
            Intent intent = new Intent(mActivity, DetailActivity.class);
            intent.putExtra("detailUrl", book.getDetailUrl());
            intent.putExtra("bid", book.getBid());
            mActivity.startActivity(intent);
        }).setOnSubItemClickListener((book, type, position) -> {
            String extra = book.getExtra();
            if (!Common.isBlank(extra)) {
                try {
                    JsonNode tree = JsonUtil.readTree(extra);
                    String booklist_id = tree.get("booklist_id").asText();
                    String title = tree.get("title").asText();
                    Intent intent = new Intent(mActivity, DetailBooklistActivity.class);
                    intent.putExtra("booklist_id", booklist_id);
                    intent.putExtra("title", title);
                    mActivity.startActivity(intent);
                } catch (Exception e) {
                    UiUtils.showToast("书籍集合参数错误");
                }
            }
        });
        rv_list.setAdapter(listBookAdapter);

        Button btn_search = view.findViewById(R.id.btn_search);
        btn_search.setOnClickListener(v -> {
            page = 1;
            showListView(true);
        });

        view.findViewById(R.id.btn_more).setOnClickListener((v) -> {
            showListView(false);
        });
    }

    private void showListView(boolean clear) {
        Button btn_more = mView.findViewById(R.id.btn_more);
        EditText et_keyword = mView.findViewById(R.id.et_keyword);
        Spinner sp_types = mView.findViewById(R.id.sp_type);
        String keyword = et_keyword.getText().toString();
        if (Common.isEmpty(keyword)) {
            keyword = et_keyword.getHint().toString();
            et_keyword.setText(keyword);
        }
        String type = sp_types.getAdapter().getItem(sp_types.getSelectedItemPosition()).toString();

        DialogLoading loading = new DialogLoading(mActivity);
        UiUtils.post(() -> {
            loading.show();
        });

        if (Common.TYPE_BL.equals(type)) {
            bookNetHelper.searchBooklist(keyword, page, new DataCallback<List<Book>>() {
                @Override
                public void call(List<Book> list, Throwable err) {
                    UiUtils.post(() -> {
                        loading.dismiss();
                        if (err != null) {
                            UiUtils.showToast("书籍搜索失败: " + err.getMessage());
                            return;
                        }
                        if (clear) {
                            listBookAdapter.getDataList().clear();
                        }
                        if (page == 1 && list.isEmpty()) {
                            UiUtils.showToast("未搜索到书籍");
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
        } else {
            List<String> languages = Common.split(SPUtils.getData(mActivity, Common.search_language_key), Common.comma);
            List<String> extensions = Common.split(SPUtils.getData(mActivity, Common.search_ext_key), Common.comma);
            bookNetHelper.search(keyword, page, languages, extensions, new DataCallback<List<Book>>() {
                @Override
                public void call(List<Book> list, Throwable err) {
                    LogUtil.d(TAG, "onResponse: book size: %d", list.size());
                    UiUtils.post(() -> {
                        loading.dismiss();
                        if (err != null) {
                            UiUtils.showToast("书籍搜索失败: " + err.getMessage());
                            return;
                        }
                        if (clear) {
                            listBookAdapter.getDataList().clear();
                        }
                        if (page == 1 && list.isEmpty()) {
                            UiUtils.showToast("未搜索到书籍");
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

}