package com.github.jsbxyyx.xbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.jsbxyyx.xbook.common.Common;
import com.github.jsbxyyx.xbook.common.LogUtil;
import com.github.jsbxyyx.xbook.data.bean.Book;
import com.github.jsbxyyx.xbook.data.bean.BookReader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jsbxyyx
 * @since 1.0
 */
public class ListBookDownloadAdapter extends RecyclerView.Adapter<ListBookDownloadAdapter.ViewHolder> {

    private final String TAG = getClass().getSimpleName();
    private Context context;
    private OnItemActionClickListener onItemActionClickListener;
    private OnItemClickListener onItemClickListener;
    private List<Book> dataList;
    private boolean imageShow;

    public interface OnItemClickListener {
        void onItemClick(Book book, int position);
    }

    public interface OnItemActionClickListener {
        void onItemActionClick(Book book, String type, int position);
    }

    public ListBookDownloadAdapter(Context context, List<Book> dataList, boolean imageShow,
                                   OnItemActionClickListener listener) {
        this.context = context;
        this.dataList = (dataList == null) ? new ArrayList<>() : dataList;
        this.imageShow = imageShow;
        this.onItemActionClickListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.book_reader_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Book book = dataList.get(position);
        holder.bind(book, imageShow);

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                LogUtil.d(TAG, "onClick item : %d", position);
                onItemClickListener.onItemClick(book, position);
            }
        });

        holder.book_reader_btn_del.setOnClickListener(v -> {
            if (onItemActionClickListener != null) {
                LogUtil.d(TAG, "onClick btn delete : %d", position);
                onItemActionClickListener.onItemActionClick(book, Common.action_delete, position);
            }
        });

        holder.book_reader_btn_download_meta.setOnClickListener(v -> {
            if (onItemActionClickListener != null) {
                LogUtil.d(TAG, "onClick btn download_meta : %d", position);
                onItemActionClickListener.onItemActionClick(book, Common.action_download_meta, position);
            }
        });

        holder.book_reader_btn_upload.setOnClickListener(v -> {
            if (onItemActionClickListener != null) {
                LogUtil.d(TAG, "onClick btn upload : %d", position);
                onItemActionClickListener.onItemActionClick(book, Common.action_upload, position);
            }
        });

        boolean exists = new File(book.extractFilePath()).exists();
        holder.book_reader_btn_file_download.setVisibility(exists ? View.GONE : View.VISIBLE);
        holder.book_reader_btn_file_download.setOnClickListener(v -> {
            if (onItemActionClickListener != null) {
                LogUtil.d(TAG, "onClick btn file download : %d", position);
                onItemActionClickListener.onItemActionClick(book, Common.action_file_download, position);
            }
        });

        holder.book_reader_image_hide.setOnClickListener(v -> {
            if (onItemActionClickListener != null) {
                LogUtil.d(TAG, "onClick image view hide : %d", position);
                onItemActionClickListener.onItemActionClick(book, Common.action_image_hide, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView book_image;
        public TextView book_title;
        public TextView book_author;
        public TextView book_file;
        public TextView book_reader_pages_total;
        public Button book_reader_btn_del;
        public Button book_reader_btn_upload;
        public Button book_reader_btn_download_meta;
        public Button book_reader_btn_file_download;
        public ImageView book_reader_image_hide;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            book_image = itemView.findViewById(R.id.book_reader_image);
            book_title = itemView.findViewById(R.id.book_reader_title);
            book_author = itemView.findViewById(R.id.book_reader_author);
            book_file = itemView.findViewById(R.id.book_reader_file);
            book_reader_pages_total = itemView.findViewById(R.id.book_reader_pages_total);
            book_reader_btn_del = itemView.findViewById(R.id.book_reader_btn_del);
            book_reader_btn_upload = itemView.findViewById(R.id.book_reader_btn_upload);
            book_reader_btn_download_meta = itemView.findViewById(R.id.book_reader_btn_download_meta);
            book_reader_btn_file_download = itemView.findViewById(R.id.book_reader_btn_file_download);
            book_reader_image_hide = itemView.findViewById(R.id.book_reader_image_hide);
        }

        public void bind(Book book, boolean imageShow) {
            Picasso.get().load(book.getCoverImage()).error(R.drawable.baseline_menu_book_24).into(book_image);
            book_image.setScaleType(ImageView.ScaleType.FIT_CENTER);
            if (!imageShow) {
                book_image.setVisibility(View.GONE);
            }
            book_title.setText(book.getTitle());
            book_author.setText(book.getAuthors());
            book_file.setText(book.getFile());

            BookReader bookReader = book.getBookReader();
            if (bookReader != null) {
                double percent = Integer.parseInt(bookReader.getCur()) * 1.0 / Integer.parseInt(bookReader.getPages());
                book_reader_pages_total.setText(
                        String.format("%s / %s / %.0f%%", bookReader.getCur(), bookReader.getPages(), Math.max(1, percent * 100))
                );
            } else {
                book_reader_pages_total.setText("-- / -- / --");
            }
        }
    }

    public List<Book> getDataList() {
        return dataList;
    }

}
