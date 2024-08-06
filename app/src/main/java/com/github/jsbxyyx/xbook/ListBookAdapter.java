package com.github.jsbxyyx.xbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.jsbxyyx.xbook.common.Common;
import com.github.jsbxyyx.xbook.data.bean.Book;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jsbxyyx
 * @since 1.0
 */
public class ListBookAdapter extends BaseAdapter {

    private ViewHolder holder;
    private Context context;
    private List<Book> dataList;

    private String TAG = "xbook";

    public ListBookAdapter(Context context, List<Book> dataList) {
        this.context = context;
        this.dataList = (dataList == null) ? new ArrayList<>() : dataList;
    }

    private static class ViewHolder {
        public ImageView book_image;
        public TextView book_title;
        public TextView book_publish;
        public TextView book_author;
        public TextView book_file;
        public TextView book_language_year;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.book_item, null);
            holder.book_image = convertView.findViewById(R.id.book_image);
            holder.book_title = convertView.findViewById(R.id.book_title);
            holder.book_publish = convertView.findViewById(R.id.book_publish);
            holder.book_author = convertView.findViewById(R.id.book_author);
            holder.book_file = convertView.findViewById(R.id.book_file);
            holder.book_language_year = convertView.findViewById(R.id.book_language_year);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Book book = dataList.get(position);

        Picasso.get().load(book.getCoverImage()).into(holder.book_image);
        holder.book_image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        holder.book_title.setText(Common.toString(book.getTitle()));
        holder.book_publish.setText(Common.toString(book.getPublisher()));
        holder.book_author.setText(Common.toString(book.getAuthors()));
        holder.book_file.setText(Common.toString(book.getFile()));
        holder.book_language_year.setText(Common.toString(book.getLanguage()) + " " + Common.toString(book.getYear()));

        return convertView;
    }

    public List<Book> getDataList() {
        return dataList;
    }

}
