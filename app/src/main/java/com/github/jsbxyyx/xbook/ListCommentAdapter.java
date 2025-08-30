package com.github.jsbxyyx.xbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.jsbxyyx.xbook.common.Common;
import com.github.jsbxyyx.xbook.data.bean.Comment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ListCommentAdapter extends RecyclerView.Adapter<ListCommentAdapter.ViewHolder> {

    private Context context;
    private List<Comment> dataList;

    public ListCommentAdapter(Context context, List<Comment> dataList) {
        this.context = context;
        this.dataList = (dataList == null) ? new ArrayList<>() : dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = dataList.get(position);
        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView user_avatar;
        public TextView user_name;
        public TextView comment_date;
        public TextView comment_text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            user_avatar = itemView.findViewById(R.id.user_avatar);
            user_name = itemView.findViewById(R.id.user_name);
            comment_date = itemView.findViewById(R.id.comment_date);
            comment_text = itemView.findViewById(R.id.comment_text);
        }

        public void bind(Comment comment) {
            Picasso.get()
                    .load(comment.getUser().getAvatar())
                    .placeholder(R.drawable.ic_default_avatar)
                    .error(R.drawable.ic_default_avatar)
                    .into(user_avatar);
            user_avatar.setScaleType(ImageView.ScaleType.FIT_CENTER);
            user_name.setText(Common.toString(comment.getUser().getName()));
            comment_date.setText(Common.toString(comment.getDateRelative()));
            comment_text.setText(Common.toString(comment.getText()));
        }
    }

    public List<Comment> getDataList() {
        return dataList;
    }

}
