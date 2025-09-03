package com.github.jsbxyyx.xbook;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.jsbxyyx.xbook.common.Common;
import com.github.jsbxyyx.xbook.common.LogUtil;
import com.github.jsbxyyx.xbook.data.bean.Book;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jsbxyyx
 * @since 1.0
 */
public class ListBookAdapter extends RecyclerView.Adapter<ListBookAdapter.ViewHolder> {

    private final String TAG = getClass().getSimpleName();
    private Context context;
    private List<Book> dataList;
    private OnItemClickListener onItemClickListener;
    private OnSubItemClickListener onSubItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Book book, int position);
    }

    public interface OnSubItemClickListener {
        void onSubItemClick(Book book, String type, int position);
    }

    public ListBookAdapter(Context context, List<Book> dataList) {
        this.context = context;
        this.dataList = (dataList == null) ? new ArrayList<>() : dataList;
    }

    public ListBookAdapter setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
        return this;
    }

    public ListBookAdapter setOnSubItemClickListener(OnSubItemClickListener listener) {
        this.onSubItemClickListener = listener;
        return this;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView book_image;
        public TextView book_title;
        public TextView book_publish;
        public TextView book_author;
        public TextView book_file;
        public TextView book_language_year;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            book_image = itemView.findViewById(R.id.book_image);
            book_title = itemView.findViewById(R.id.book_title);
            book_publish = itemView.findViewById(R.id.book_publish);
            book_author = itemView.findViewById(R.id.book_author);
            book_file = itemView.findViewById(R.id.book_file);
            book_language_year = itemView.findViewById(R.id.book_language_year);
        }

        public void bind(Book book) {
            Picasso.get().load(book.getCoverImage()).error(R.drawable.baseline_menu_book_24).into(book_image);
            book_image.setScaleType(ImageView.ScaleType.FIT_CENTER);
            book_title.setText(Common.toString(book.getTitle()));
            book_publish.setText(Common.toString(book.getPublisher()));
            book_author.setText(Common.toString(book.getAuthors()));
            book_file.setText(Common.toString(book.getFile()));
            book_language_year.setText(Common.toString(book.getLanguage()) + " " + Common.toString(book.getYear()));
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.book_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Book book = dataList.get(position);
        holder.bind(book);

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(book, position);
            }
        });

        if (Book.content_type_booklist.equals(book.getContent_type())) {
            holder.book_publish.setTextColor(Color.parseColor("#03A9F4"));
            holder.book_publish.setOnClickListener(v -> {
                if (onSubItemClickListener != null) {
                    onSubItemClickListener.onSubItemClick(book, Book.publisher_key, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        int count = dataList.size();
        LogUtil.d(TAG, "getItemCount: " + count);
        return count;
    }

    public List<Book> getDataList() {
        return dataList;
    }

}
