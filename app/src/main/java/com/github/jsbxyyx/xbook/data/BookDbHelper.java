package com.github.jsbxyyx.xbook.data;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.github.jsbxyyx.xbook.common.LogUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author jsbxyyx
 * @since 1.0
 */
public class BookDbHelper extends SQLiteOpenHelper {

    private String TAG = getClass().getName();

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "xbook.db";

    private final ReentrantLock l = new ReentrantLock();

    private String t_book = "book";
    private String f_book_id = "id";
    private String f_book_bid = "book_id";
    private String f_book_isbn = "isbn";
    private String f_book_img = "img";
    private String f_book_title = "title";
    private String f_book_publisher = "publisher";
    private String f_book_authors = "authors";
    private String f_book_file = "file";
    private String f_book_language = "language";
    private String f_book_year = "year";
    private String f_book_detail_url = "detail_url";
    private String f_book_download_url = "download_url";
    private String f_book_remark = "remark";
    private String f_book_created = "created";
    private String f_book_user = "user";

    private String t_book_reader = "book_reader";
    private String f_book_reader_id = "id";
    private String f_book_reader_book_id = "book_id";
    private String f_book_reader_cur = "cur";
    private String f_book_reader_pages = "pages";
    private String f_book_reader_created = "created";
    private String f_book_reader_user = "user";
    private String f_book_reader_remark = "remark";

    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists " + t_book + " ( " +
                f_book_id + " INTEGER primary key AUTOINCREMENT, " +
                f_book_bid + " TEXT not null, " +
                f_book_isbn + " TEXT not null, " +
                f_book_img + " TEXT not null, " +
                f_book_title + " TEXT not null, " +
                f_book_publisher + " TEXT, " +
                f_book_authors + " TEXT, " +
                f_book_file + " TEXT, " +
                f_book_language + " TEXT, " +
                f_book_year + " TEXT, " +
                f_book_detail_url + " TEXT not null, " +
                f_book_download_url + " TEXT not null, " +
                f_book_remark + " TEXT, " +
                f_book_created + " DATETIME not null," +
                f_book_user + " TEXT not null " +
                ")");

        db.execSQL("create table if not exists " + t_book_reader + " ( " +
                f_book_reader_id + " INTEGER primary key AUTOINCREMENT, " +
                f_book_reader_book_id + " INTEGER not null, " +
                f_book_reader_cur + " INTEGER not null, " +
                f_book_reader_pages + " INTEGER not null, " +
                f_book_reader_created + " DATETIME not null, " +
                f_book_reader_user + " TEXT not null, " +
                f_book_reader_remark + " TEXT " +
                ")");
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LogUtil.w(TAG, "onUpgrade: !!!");
        onCreate(db);
    }

    public void insertBook(Book e) {
        try {
            l.lock();
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(f_book_id, e.getId());
            values.put(f_book_bid, e.getBid());
            values.put(f_book_isbn, e.getIsbn());
            values.put(f_book_img, e.getCoverImage());
            values.put(f_book_title, e.getTitle());
            values.put(f_book_publisher, e.getPublisher());
            values.put(f_book_authors, e.getAuthors());
            values.put(f_book_file, e.getFile());
            values.put(f_book_language, e.getLanguage());
            values.put(f_book_year, e.getYear());
            values.put(f_book_detail_url, e.getDetailUrl());
            values.put(f_book_download_url, e.getDownloadUrl());
            values.put(f_book_remark, e.getRemark());
            values.put(f_book_created, new Date().getTime());
            values.put(f_book_user, e.getUser());

            db.insert(t_book, null, values);
        } finally {
            l.unlock();
        }
    }

    public Book findBookByBid(String bid) {
        try {
            l.lock();
            SQLiteDatabase db = getWritableDatabase();
            String sql = String.format("select * from %s where %s=?", t_book, f_book_bid);
            try (Cursor cursor = db.rawQuery(sql, new String[]{bid})) {
                if (cursor.moveToFirst()) {
                    Book e = new Book();
                    buildBook(cursor, e);
                    return e;
                }
                return null;
            }
        } finally {
            l.unlock();
        }
    }

    public Book findBookById(String id) {
        try {
            l.lock();
            SQLiteDatabase db = getWritableDatabase();
            String sql = String.format("select * from %s where %s=?", t_book, f_book_id);
            try (Cursor cursor = db.rawQuery(sql, new String[]{id})) {
                if (cursor.moveToFirst()) {
                    Book e = new Book();
                    buildBook(cursor, e);
                    return e;
                }
                return null;
            }
        } finally {
            l.unlock();
        }
    }

    public List<Book> findAllBook() {
        try {
            l.lock();
            List<Book> list = new ArrayList<>();
            SQLiteDatabase db = getWritableDatabase();
            String sql = String.format("select * from %s", t_book);
            try (Cursor cursor = db.rawQuery(sql, null)) {
                if (cursor.moveToFirst()) {
                    do {
                        Book e = new Book();
                        buildBook(cursor, e);
                        list.add(e);
                    } while (cursor.moveToNext());
                }
            }
            return list;
        } finally {
            l.unlock();
        }
    }

    public BookReader findBookReaderByBookId(Long bookId) {
        try {
            l.lock();
            SQLiteDatabase db = getWritableDatabase();
            String sql = String.format("select * from %s where %s=?", t_book_reader, f_book_reader_book_id);
            try (Cursor cursor = db.rawQuery(sql, new String[]{bookId + ""})) {
                if (cursor.moveToFirst()) {
                    BookReader e = new BookReader();
                    buildBookReader(cursor, e);
                    return e;
                }
                return null;
            }
        } finally {
            l.unlock();
        }
    }

    public void deleteBook(Long id) {
        try {
            l.lock();
            SQLiteDatabase db = getWritableDatabase();
            String sql1 = String.format("delete from %s where %s=?", t_book, f_book_id);
            db.execSQL(sql1, new Object[]{id});
            String sql2 = String.format("delete from %s where %s=?", t_book_reader, f_book_reader_book_id);
            db.execSQL(sql2, new Object[]{id});
        } finally {
            l.unlock();
        }
    }

    public void updateBook(Book book) {
        try {
            l.lock();
            SQLiteDatabase db = getWritableDatabase();
            String sql1 = String.format("update %s set %s=?, %s=?, %s=?, %s=?, %s=?, %s=?, %s=?, %s=?, %s=?, %s=?, %s=?, %s=?, %s=?, %s=? where %s=?",
                    t_book,
                    f_book_bid, f_book_isbn, f_book_img, f_book_title, f_book_publisher,
                    f_book_authors, f_book_file, f_book_language, f_book_year, f_book_detail_url,
                    f_book_download_url, f_book_remark, f_book_created, f_book_user,
                    f_book_id);
            db.execSQL(sql1, new Object[]{
                    book.getBid(), book.getIsbn(), book.getCoverImage(), book.getTitle(),
                    book.getPublisher(), book.getAuthors(), book.getFile(), book.getLanguage(),
                    book.getYear(), book.getDetailUrl(), book.getDownloadUrl(), book.getRemark(),
                    book.getCreated(), book.getUser(),
                    book.getId()
            });
        } finally {
            l.unlock();
        }
    }

    public void insertBookReader(BookReader e) {
        try {
            l.lock();
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(f_book_reader_book_id, e.getBookId());
            values.put(f_book_reader_cur, e.getCur());
            values.put(f_book_reader_pages, e.getPages());
            values.put(f_book_reader_created, new Date().getTime());
            values.put(f_book_reader_user, e.getUser());
            values.put(f_book_reader_remark, e.getRemark());
            db.insert(t_book_reader, null, values);
        } finally {
            l.unlock();
        }
    }

    public void updateBookReaderByBookId(BookReader e) {
        try {
            l.lock();
            SQLiteDatabase db = getWritableDatabase();
            String sql = String.format("update %s set %s=?, %s=?, %s=? where %s=?",
                    t_book_reader,
                    f_book_reader_cur, f_book_reader_pages, f_book_reader_remark,
                    f_book_reader_book_id);
            db.execSQL(sql, new Object[]{e.getCur(), e.getPages(), e.getRemark(), e.getBookId()});
        } finally {
            l.unlock();
        }
    }

    @SuppressLint("Range")
    private void buildBook(Cursor cursor, Book e) {
        e.setId(cursor.getLong(cursor.getColumnIndex(f_book_id)));
        e.setBid(cursor.getString(cursor.getColumnIndex(f_book_bid)));
        e.setIsbn(cursor.getString(cursor.getColumnIndex(f_book_isbn)));
        e.setCoverImage(cursor.getString(cursor.getColumnIndex(f_book_img)));
        e.setTitle(cursor.getString(cursor.getColumnIndex(f_book_title)));
        e.setPublisher(cursor.getString(cursor.getColumnIndex(f_book_publisher)));
        e.setAuthors(cursor.getString(cursor.getColumnIndex(f_book_authors)));
        e.setFile(cursor.getString(cursor.getColumnIndex(f_book_file)));
        e.setLanguage(cursor.getString(cursor.getColumnIndex(f_book_language)));
        e.setYear(cursor.getString(cursor.getColumnIndex(f_book_year)));
        e.setDetailUrl(cursor.getString(cursor.getColumnIndex(f_book_detail_url)));
        e.setDownloadUrl(cursor.getString(cursor.getColumnIndex(f_book_download_url)));
        e.setRemark(cursor.getString(cursor.getColumnIndex(f_book_remark)));
        e.setCreated(cursor.getString(cursor.getColumnIndex(f_book_created)));
        e.setUser(cursor.getString(cursor.getColumnIndex(f_book_user)));
        e.setBookReader(findBookReaderByBookId(e.getId()));
    }

    @SuppressLint("Range")
    private void buildBookReader(Cursor cursor, BookReader e) {
        e.setId(cursor.getString(cursor.getColumnIndex(f_book_reader_id)));
        e.setBookId(cursor.getString(cursor.getColumnIndex(f_book_reader_book_id)));
        e.setCur(cursor.getString(cursor.getColumnIndex(f_book_reader_cur)));
        e.setPages(cursor.getString(cursor.getColumnIndex(f_book_reader_pages)));
        e.setCreated(cursor.getString(cursor.getColumnIndex(f_book_reader_created)));
        e.setUser(cursor.getString(cursor.getColumnIndex(f_book_reader_user)));
        e.setRemark(cursor.getString(cursor.getColumnIndex(f_book_reader_remark)));
    }
}
