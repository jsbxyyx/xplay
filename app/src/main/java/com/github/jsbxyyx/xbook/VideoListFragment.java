package com.github.jsbxyyx.xbook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.github.jsbxyyx.xbook.common.Common;
import com.github.jsbxyyx.xbook.common.DataCallback;
import com.github.jsbxyyx.xbook.common.LogUtil;
import com.github.jsbxyyx.xbook.data.VideoNetHelper;
import com.github.jsbxyyx.xbook.data.bean.QqVideo;

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

        Button btn_video_search = view.findViewById(R.id.btn_video_search);
        btn_video_search.setOnClickListener(v -> {
            showListView(true);
        });
    }

    private void showListView(boolean clear) {
        EditText et_video_keyword = mView.findViewById(R.id.et_video_keyword);
        String q = et_video_keyword.getText().toString();
        if (Common.isEmpty(q)) {
            q = et_video_keyword.getHint().toString();
        }
        videoNetHelper.search(q, new DataCallback<List<QqVideo>>() {
            @Override
            public void call(List<QqVideo> list, Throwable err) {
                LogUtil.d(TAG, "onResponse: video size: %d", list.size());
                mActivity.runOnUiThread(() -> {
                    if (err != null) {
                        Toast.makeText(mActivity, "err: " + err.getMessage(), Toast.LENGTH_LONG).show();
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

}