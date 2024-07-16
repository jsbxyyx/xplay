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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.jsbxyyx.xbook.common.Common;
import com.github.jsbxyyx.xbook.common.DataCallback;
import com.github.jsbxyyx.xbook.common.LogUtil;
import com.github.jsbxyyx.xbook.data.VideoNetHelper;
import com.github.jsbxyyx.xbook.data.bean.QqVideo;
import com.github.jsbxyyx.xbook.data.bean.QqVideoHotRank;
import com.github.jsbxyyx.xbook.data.bean.QqVideoHotWord;

import java.util.List;

/**
 * @author jsbxyyx
 */
public class VideoListFragment extends Fragment {

    private String TAG = getClass().getSimpleName();

    private View mView;
    private Activity mActivity;
    private ListView lv_video_list;
    private ListVideoAdapter listVideoAdapter;
    private VideoNetHelper videoNetHelper;
    private AutoLinearLayout ll_hot_rank;

    private int page = 1;

    public VideoListFragment() {
        videoNetHelper = new VideoNetHelper();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_list, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mView = view;
        mActivity = getActivity();

        lv_video_list = view.findViewById(R.id.lv_video_list);
        Button btn_video_search = view.findViewById(R.id.btn_video_search);
        Button btn_hot_search = view.findViewById(R.id.btn_hot_search);
        ll_hot_rank = view.findViewById(R.id.ll_hot_rank);

        listVideoAdapter = new ListVideoAdapter(mActivity, null, new ListItemClickListener() {
            @Override
            public void onClick(View view, String type, int position) {
                LogUtil.d(TAG, "%s, %d", type, position);
                QqVideo video = listVideoAdapter.getDataList().get(Integer.parseInt(type));
                QqVideo.QqPlaylist playlist = video.getPlaylist().get(position);
                String playUrl = video.getPlayer() + playlist.getUrl();
                LogUtil.d(TAG, "%s", playUrl);
                Intent intent = new Intent(mActivity, VideoViewActivity2.class);
                intent.putExtra("playUrl", playUrl);
                startActivity(intent);
            }
        });
        lv_video_list.setAdapter(listVideoAdapter);

        btn_video_search.setOnClickListener(v -> {
            showListView(true);
        });

        btn_hot_search.setOnClickListener(v -> {
            hotSearch();
        });

        hotSearch();
    }

    private void showListView(boolean clear) {
        EditText et_video_keyword = mView.findViewById(R.id.et_video_keyword);
        String q = et_video_keyword.getText().toString();
        if (Common.isEmpty(q)) {
            q = et_video_keyword.getHint().toString();
            et_video_keyword.setText(q);
        }
        LoadingDialog loading = new LoadingDialog(mActivity);
        mActivity.runOnUiThread(() -> {
            loading.show();
        });
        videoNetHelper.search(q, new DataCallback<List<QqVideo>>() {
            @Override
            public void call(List<QqVideo> list, Throwable err) {
                LogUtil.d(TAG, "onResponse: video size: %d", list.size());
                mActivity.runOnUiThread(() -> {
                    loading.dismiss();
                    if (err != null) {
                        Toast.makeText(mActivity, "搜索视频失败: " + err.getMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (clear) {
                        listVideoAdapter.getDataList().clear();
                    }
                    listVideoAdapter.getDataList().addAll(list);
                    listVideoAdapter.notifyDataSetChanged();
                });
            }
        });
    }

    private void hotSearch() {
        videoNetHelper.hotWord(new DataCallback<List<QqVideoHotWord>>() {
            @Override
            public void call(List<QqVideoHotWord> list, Throwable err) {
                mActivity.runOnUiThread(() -> {
                    if (err != null) {
                        Toast.makeText(mActivity, "获取热搜失败", Toast.LENGTH_LONG).show();
                    }
                    ll_hot_rank.removeAllViews();
                    if (list != null && !list.isEmpty()) {
                        for (QqVideoHotWord hotWord : list) {
                            TextView tv = new TextView(mActivity);
                            tv.setText(hotWord.getSearchWord());
                            tv.setOnClickListener(v -> {
                                EditText et_video_keyword = mView.findViewById(R.id.et_video_keyword);
                                TextView _tv = (TextView) v;
                                et_video_keyword.setText(_tv.getText());
                                showListView(true);
                            });
                            ll_hot_rank.addView(tv);
                        }
                    }
                });
            }
        });
    }

}