package com.github.jsbxyyx.xbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.jsbxyyx.xbook.common.Common;
import com.github.jsbxyyx.xbook.data.bean.Comment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ListCommentAdapter extends BaseAdapter {

    private ViewHolder holder;
    private Context context;
    private List<Comment> dataList;

    public ListCommentAdapter(Context context, List<Comment> dataList) {
        this.context = context;
        this.dataList = (dataList == null) ? new ArrayList<>() : dataList;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.comment_item, null);
            holder.user_avatar = convertView.findViewById(R.id.user_avatar);
            holder.user_name = convertView.findViewById(R.id.user_name);
            holder.comment_date = convertView.findViewById(R.id.comment_date);
            holder.comment_text = convertView.findViewById(R.id.comment_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Comment comment = dataList.get(position);

        Picasso.get().load(comment.getUser().getAvatar()).into(holder.user_avatar);
        holder.user_avatar.setScaleType(ImageView.ScaleType.FIT_CENTER);
        holder.user_name.setText(Common.toString(comment.getUser().getName()));
        holder.comment_date.setText(Common.toString(comment.getDateRelative()));
        holder.comment_text.setText(Common.toString(comment.getText()));

        return convertView;
    }

    private static class ViewHolder {
        public ImageView user_avatar;
        public TextView user_name;
        public TextView comment_date;
        public TextView comment_text;
    }

    public List<Comment> getDataList() {
        return dataList;
    }

}
