package com.github.jsbxyyx.xbook;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.jsbxyyx.xbook.common.Common;
import com.github.jsbxyyx.xbook.common.LogUtil;
import com.github.jsbxyyx.xbook.data.Book;
import com.github.jsbxyyx.xbook.data.BookReader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jsbxyyx
 * @since 1.0
 */
public class ListBookDownloadAdapter extends BaseAdapter {

    private ViewHolder holder;
    private Context context;
    private ListItemClickListener listItemClickListener;
    private List<Book> dataList;

    private String TAG = "xbook";

    public ListBookDownloadAdapter(Context context, List<Book> dataList,
                                   ListItemClickListener listItemClickListener) {
        this.context = context;
        this.dataList = (dataList == null) ? new ArrayList<>() : dataList;
        this.listItemClickListener = listItemClickListener;
    }

    private static class ViewHolder {
        public ImageView book_image;
        public TextView book_title;
        public TextView book_file;
        public TextView book_reader_pages_total;
        public Button book_reader_btn_del;
        public Button book_reader_btn_upload;
        public Button book_reader_btn_file_download;
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

    // 注意原本getView方法中的int position变量是非final的，现在改为final
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.book_reader_item, null);
            holder.book_image = convertView.findViewById(R.id.book_reader_image);
            holder.book_title = convertView.findViewById(R.id.book_reader_title);
            holder.book_file = convertView.findViewById(R.id.book_reader_file);
            holder.book_reader_pages_total = convertView.findViewById(R.id.book_reader_pages_total);
            holder.book_reader_btn_del = convertView.findViewById(R.id.book_reader_btn_del);
            holder.book_reader_btn_upload = convertView.findViewById(R.id.book_reader_btn_upload);
            holder.book_reader_btn_file_download = convertView.findViewById(R.id.book_reader_btn_file_download);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Book book = dataList.get(position);
        BookReader bookReader = book.getBookReader();

        Picasso.get().load(book.getCoverImage()).into(holder.book_image);
        holder.book_image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        holder.book_title.setText(book.getTitle());
        holder.book_file.setText(book.getFile());
        if (bookReader != null) {
            double percent = Integer.parseInt(bookReader.getCur()) * 1.0 / Integer.parseInt(bookReader.getPages());
            holder.book_reader_pages_total.setText(
                    String.format("%s / %s / %.0f%%", bookReader.getCur(), bookReader.getPages(), Math.max(1, percent * 100))
            );
        } else {
            holder.book_reader_pages_total.setText("-- / -- / --");
        }

        holder.book_reader_btn_del.setTag(position);
        holder.book_reader_btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listItemClickListener != null) {
                    int position = (int) v.getTag();
                    LogUtil.d(TAG, "onClick btn delete : %d", position);
                    listItemClickListener.onClick(v, Common.action_delete, position);
                }
            }
        });

        holder.book_reader_btn_upload.setTag(position);
        holder.book_reader_btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listItemClickListener != null) {
                    int position = (int) v.getTag();
                    LogUtil.d(TAG, "onClick btn upload : %d", position);
                    listItemClickListener.onClick(v, Common.action_upload, position);
                }
            }
        });


        boolean exists = new File(book.getRemarkProperty("file_path")).exists();
        holder.book_reader_btn_file_download.setTag(position);
        holder.book_reader_btn_file_download.setVisibility(exists ? View.GONE : View.VISIBLE);
        holder.book_reader_btn_file_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listItemClickListener != null) {
                    int position = (int) v.getTag();
                    LogUtil.d(TAG, "onClick btn file download : %d", position);
                    listItemClickListener.onClick(v, Common.action_file_download, position);
                }
            }
        });
        return convertView;
    }

    public List<Book> getDataList() {
        return dataList;
    }

}
