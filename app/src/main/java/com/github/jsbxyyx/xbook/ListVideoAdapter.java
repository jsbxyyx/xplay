package com.github.jsbxyyx.xbook;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.jsbxyyx.xbook.common.Common;
import com.github.jsbxyyx.xbook.common.LogUtil;
import com.github.jsbxyyx.xbook.data.bean.QqVideo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jsbxyyx
 */
public class ListVideoAdapter extends RecyclerView.Adapter<ListVideoAdapter.ViewHolder> {

    private final String TAG = getClass().getSimpleName();
    private Context context;
    private List<QqVideo> dataList;
    private OnSubItemClickListener onSubItemClickListener;

    public interface OnSubItemClickListener {
        void onItemClick(QqVideo video, int subPosition);
    }

    public ListVideoAdapter(Context context, List<QqVideo> list) {
        this.context = context;
        this.dataList = list == null ? new ArrayList<>() : list;
    }

    public void setOnSubItemClickListener(OnSubItemClickListener listener) {
        this.onSubItemClickListener = listener;
    }

    public List<QqVideo> getDataList() {
        return dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.video_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QqVideo video = dataList.get(position);
        holder.bind(video);

        List<QqVideo.QqPlaylist> playlist = video.getPlaylist();
        int subPosition = 0;
        holder.video_playlist.removeAllViews();
        if (playlist != null) {
            for (QqVideo.QqPlaylist pl : playlist) {
                TextView textView = new TextView(context);
                AutoLinearLayout.LayoutParams params = new AutoLinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(20, 20, 10, 0);
                textView.setLayoutParams(params);
                textView.setTextSize(23);
                textView.setText(pl.getTitle());
                if (!Common.isEmpty(pl.getMarkLabel()) && pl.getMarkLabel().contains("VIP")) {
                    textView.setTextColor(Color.parseColor("#ebd078"));
                }
                final int finalSubPosition = subPosition;
                textView.setOnClickListener(v -> {
                    if (onSubItemClickListener != null) {
                        LogUtil.d(TAG, "onSubItemClickListener: %d", finalSubPosition);
                        onSubItemClickListener.onItemClick(video, finalSubPosition);
                    }
                });
                holder.video_playlist.addView(textView);
                subPosition++;
            }
        }
    }

    @Override
    public int getItemCount() {
        int count = dataList.size();
        LogUtil.d(TAG, "getItemCount: " + count);
        return count;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView video_image;
        public TextView video_name;
        public TextView video_desc;
        public LinearLayout video_playlist;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            video_image = itemView.findViewById(R.id.video_image);
            video_name = itemView.findViewById(R.id.video_name);
            video_desc = itemView.findViewById(R.id.video_desc);
            video_playlist = itemView.findViewById(R.id.ll_playlist);
        }

        public void bind(QqVideo video) {
            Picasso.get().load(video.getCoverImage()).error(R.drawable.baseline_live_tv_24).into(video_image);
            video_image.setScaleType(ImageView.ScaleType.FIT_CENTER);

            video_name.setText(Common.toString(video.getName()));
            video_desc.setText(Common.toString(video.getDescText()));
        }
    }

}
