package com.github.jsbxyyx.xbook;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.jsbxyyx.xbook.common.Common;
import com.github.jsbxyyx.xbook.data.bean.QqVideo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jsbxyyx
 */
public class ListVideoAdapter extends BaseAdapter {

    private ViewHolder holder;
    private Context mContext;
    private List<QqVideo> dataList;
    private ListItemClickListener mListener;

    public ListVideoAdapter(Context context, List<QqVideo> list, ListItemClickListener listener) {
        mContext = context;
        dataList = list == null ? new ArrayList<>() : list;
        mListener = listener;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.video_item, null);
            holder.video_image = convertView.findViewById(R.id.video_image);
            holder.video_name = convertView.findViewById(R.id.video_name);
            holder.video_desc = convertView.findViewById(R.id.video_desc);
            holder.video_playlist = convertView.findViewById(R.id.ll_playlist);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        QqVideo video = dataList.get(position);

        Picasso.get().load(video.getCoverImage()).into(holder.video_image);
        holder.video_image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        holder.video_name.setText(video.getName());
        holder.video_desc.setText(video.getDescText());

        List<QqVideo.QqPlaylist> playlist = video.getPlaylist();
        int subPosition = 0;
        holder.video_playlist.removeAllViews();
        for (QqVideo.QqPlaylist pl : playlist) {
            TextView textView = new TextView(mContext);
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
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onClick(v, position + "", finalSubPosition);
                    }
                }
            });
            holder.video_playlist.addView(textView);
            subPosition++;
        }

        return convertView;
    }

    public List<QqVideo> getDataList() {
        return dataList;
    }

    private static class ViewHolder {
        public ImageView video_image;
        public TextView video_name;
        public TextView video_desc;
        public LinearLayout video_playlist;
    }

}
