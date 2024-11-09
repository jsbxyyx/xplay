package com.github.jsbxyyx.xbook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.jsbxyyx.xbook.common.Common;
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

    private String TAG = "xbook";

    private View mView;
    private Activity mActivity;
    private ListView lv_list;
    private ListBookAdapter lvListAdapter;
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

        lv_list = view.findViewById(R.id.lv_list);
        lvListAdapter = new ListBookAdapter(mActivity, null);
        lv_list.setAdapter(lvListAdapter);

        lv_list.setOnItemClickListener((parent, view1, position, id) -> {
            Book t = (Book) lv_list.getAdapter().getItem(position);
            LogUtil.d(TAG, "lv_list: setOnItemClickListener: %s", t);
            Intent intent = new Intent(mActivity, DetailActivity.class);
            intent.putExtra("detailUrl", t.getDetailUrl());
            mActivity.startActivity(intent);
        });

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
        String keyword = et_keyword.getText().toString();
        if (Common.isEmpty(keyword)) {
            keyword = et_keyword.getHint().toString();
            et_keyword.setText(keyword);
        }

        LoadingDialog loading = new LoadingDialog(mActivity);
        mActivity.runOnUiThread(() -> {
            loading.show();
        });
        List<String> languages = Common.split(SPUtils.getData(mActivity, Common.search_language_key), Common.comma);
        List<String> extensions = Common.split(SPUtils.getData(mActivity, Common.search_ext_key), Common.comma);
        bookNetHelper.search(keyword, page, languages, extensions, (list, err) -> {
            LogUtil.d(TAG, "onResponse: book size: %d", list.size());
            mActivity.runOnUiThread(() -> {
                loading.dismiss();
                if (err != null) {
                    UiUtils.showToast("书籍搜索失败: " + err.getMessage());
                    return;
                }
                if (clear) {
                    lvListAdapter.getDataList().clear();
                }
                if (page == 1 && list.isEmpty()) {
                    UiUtils.showToast("未搜索到书籍");
                    return;
                }
                lvListAdapter.getDataList().addAll(list);
                lvListAdapter.notifyDataSetChanged();
                if (!list.isEmpty()) {
                    btn_more.setVisibility(View.VISIBLE);
                } else {
                    btn_more.setVisibility(View.GONE);
                }
                page += 1;
            });
        });
    }

}