package com.github.jsbxyyx.xbook.data;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.github.jsbxyyx.xbook.common.AppUtils;
import com.github.jsbxyyx.xbook.common.LogUtil;
import com.github.jsbxyyx.xbook.common.UiUtils;
import com.github.jsbxyyx.xbook.data.bean.Book;
import com.github.jsbxyyx.xbook.data.bean.BookReader;
import com.github.jsbxyyx.xbook.data.bean.TableBook;
import com.github.jsbxyyx.xbook.data.bean.TableBookReader;
import com.github.jsbxyyx.xbook.data.bean.TableField;
import com.github.jsbxyyx.xbook.data.bean.TableViewTime;
import com.github.jsbxyyx.xbook.data.bean.ViewTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author jsbxyyx
 * @since 1.0
 */
public class BookDbHelper extends SQLiteOpenHelper {

    private final String TAG = getClass().getName();

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "xbook.db";

    private final ReentrantLock l = new ReentrantLock();

    private static volatile BookDbHelper instance;

    private BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static BookDbHelper getInstance() {
        if (instance == null) {
            synchronized (BookDbHelper.class) {
                if (instance == null) {
                    instance = new BookDbHelper(AppUtils.getContext());
                }
            }
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        TableBook t1 = new TableBook();
        String s1 = t1.create(false, true, "");
        db.execSQL(s1);

        TableBookReader t2 = new TableBookReader();
        String s2 = t2.create(false, true, "");
        db.execSQL(s2);

        TableViewTime t3 = new TableViewTime();
        String s3 = t3.create(false, true, "");
        db.execSQL(s3);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LogUtil.w(TAG, "onUpgrade: !!! old:" + oldVersion + " new:" + newVersion);
        onCreate(db);
        if (oldVersion < 2) {
            try {
                db.beginTransaction();

                TableBookReader t = new TableBookReader();
                String suffix = "_backup";
                String backup_create = t.create(true, false, suffix);
                String backup_table_name = t.getTableName() + suffix;
                String origin_table_name = t.getTableName();
                String origin_create = t.create(false, false, "");

                LogUtil.i(TAG, "[" + backup_create + "]");
                db.execSQL(backup_create);
                String insert_backup_sql = "INSERT INTO " + backup_table_name + " SELECT " + t.getAllFieldString() + " FROM " + origin_table_name;
                LogUtil.i(TAG, "[" + insert_backup_sql + "]");
                db.execSQL(insert_backup_sql);
                String drop_origin_sql = "DROP TABLE " + origin_table_name;
                LogUtil.i(TAG, "[" + drop_origin_sql + "]");
                db.execSQL(drop_origin_sql);
                LogUtil.i(TAG, "[" + origin_create + "]");
                db.execSQL(origin_create);
                String insert_origin_sql = "INSERT INTO " + origin_table_name + " SELECT " + t.getAllFieldString() + " FROM " + backup_table_name;
                LogUtil.i(TAG, "[" + insert_origin_sql + "]");
                db.execSQL(insert_origin_sql);
                String drop_backup_sql = "DROP TABLE " + backup_table_name;
                LogUtil.i(TAG, "[" + drop_backup_sql + "]");
                db.execSQL(drop_backup_sql);

                db.endTransaction();
            } catch (Exception e) {
                LogUtil.e(TAG, LogUtil.getStackTraceString(e));
                UiUtils.showToast("升级失败，请卸载重新安装");
            }
        }
    }

    public void insertBook(Book e) {
        try {
            l.lock();
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();

            TableBook t = new TableBook();

            values.put(t.id.getName(), e.getId());
            values.put(t.bid.getName(), e.getBid());
            values.put(t.isbn.getName(), e.getIsbn());
            values.put(t.img.getName(), e.getCoverImage());
            values.put(t.title.getName(), e.getTitle());
            values.put(t.publisher.getName(), e.getPublisher());
            values.put(t.authors.getName(), e.getAuthors());
            values.put(t.file.getName(), e.getFile());
            values.put(t.language.getName(), e.getLanguage());
            values.put(t.year.getName(), e.getYear());
            values.put(t.detail_url.getName(), e.getDetailUrl());
            values.put(t.download_url.getName(), e.getDownloadUrl());
            values.put(t.remark.getName(), e.getRemark());
            values.put(t.created.getName(), new Date().getTime());
            values.put(t.user.getName(), e.getUser());

            db.insert(t.getTableName(), null, values);
        } finally {
            l.unlock();
        }
    }

    public Book findBookByBid(String bid) {
        try {
            l.lock();
            SQLiteDatabase db = getWritableDatabase();

            TableBook t = new TableBook();
            String sql = t.selectAll(t.bid);
            String[] params = new String[]{bid};
            LogUtil.d(TAG, "sql:[%s] params:%s", sql, Arrays.toString(params));
            try (Cursor cursor = db.rawQuery(sql, params)) {
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
            TableBook t = new TableBook();
            String sql = t.selectAll(t.id);
            String[] params = new String[]{id};
            LogUtil.d(TAG, "sql:[%s] params:%s", sql, Arrays.toString(params));
            try (Cursor cursor = db.rawQuery(sql, params)) {
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

            TableBook t = new TableBook();
            String sql = t.selectAll();
            String[] params = new String[]{};
            LogUtil.d(TAG, "sql:[%s] params:%s", sql, Arrays.toString(params));
            try (Cursor cursor = db.rawQuery(sql, params)) {
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
            TableBookReader t = new TableBookReader();
            String sql = t.selectAll(t.book_id);
            String[] params = new String[]{bookId + ""};
            LogUtil.d(TAG, "sql:[%s] params:%s", sql, Arrays.toString(params));
            try (Cursor cursor = db.rawQuery(sql, params)) {
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

            TableBook t1 = new TableBook();
            String sql1 = t1.delete(t1.id);
            Object[] params1 = new Object[]{id};
            LogUtil.d(TAG, "sql:[%s] params:%s", sql1, Arrays.toString(params1));
            db.execSQL(sql1, params1);

            TableBookReader t2 = new TableBookReader();
            String sql2 = t2.delete(t2.book_id);
            Object[] params2 = new Object[]{id};
            LogUtil.d(TAG, "sql:[%s] params:%s", sql2, Arrays.toString(params2));
            db.execSQL(sql2, params2);
        } finally {
            l.unlock();
        }
    }

    public void updateBook(Book book) {
        try {
            l.lock();
            SQLiteDatabase db = getWritableDatabase();
            TableBook t1 = new TableBook();
            String sql1 = t1.update(t1.getAllField(t1.id).toArray(new TableField[0]), t1.id);
            Object[] params = new Object[]{
                    book.getBid(), book.getIsbn(), book.getCoverImage(), book.getTitle(),
                    book.getPublisher(), book.getAuthors(), book.getFile(), book.getLanguage(),
                    book.getYear(), book.getDetailUrl(), book.getDownloadUrl(), book.getRemark(),
                    book.getCreated(), book.getUser(),
                    book.getId()
            };
            LogUtil.d(TAG, "sql:[%s] params:%s", sql1, Arrays.toString(params));
            db.execSQL(sql1, params);
        } finally {
            l.unlock();
        }
    }

    public void insertBookReader(BookReader e) {
        try {
            l.lock();
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            TableBookReader t = new TableBookReader();
            values.put(t.id.getName(), e.getId());
            values.put(t.book_id.getName(), e.getBookId());
            values.put(t.cur.getName(), e.getCur());
            values.put(t.pages.getName(), e.getPages());
            values.put(t.created.getName(), new Date().getTime());
            values.put(t.user.getName(), e.getUser());
            values.put(t.remark.getName(), e.getRemark());
            db.insert(t.getTableName(), null, values);
        } finally {
            l.unlock();
        }
    }

    public void updateBookReaderByBookId(BookReader e) {
        try {
            l.lock();
            SQLiteDatabase db = getWritableDatabase();
            TableBookReader t = new TableBookReader();
            String sql = t.update(new TableField[]{t.cur, t.pages, t.remark}, t.book_id);
            Object[] params = new Object[]{e.getCur(), e.getPages(), e.getRemark(), e.getBookId()};
            LogUtil.d(TAG, "sql:[%s] params:%s", sql, Arrays.toString(params));
            db.execSQL(sql, params);
        } finally {
            l.unlock();
        }
    }

    @SuppressLint("Range")
    private void buildBook(Cursor cursor, Book e) {
        TableBook t = new TableBook();
        e.setId(cursor.getLong(cursor.getColumnIndex(t.id.getName())));
        e.setBid(cursor.getString(cursor.getColumnIndex(t.bid.getName())));
        e.setIsbn(cursor.getString(cursor.getColumnIndex(t.isbn.getName())));
        e.setCoverImage(cursor.getString(cursor.getColumnIndex(t.img.getName())));
        e.setTitle(cursor.getString(cursor.getColumnIndex(t.title.getName())));
        e.setPublisher(cursor.getString(cursor.getColumnIndex(t.publisher.getName())));
        e.setAuthors(cursor.getString(cursor.getColumnIndex(t.authors.getName())));
        e.setFile(cursor.getString(cursor.getColumnIndex(t.file.getName())));
        e.setLanguage(cursor.getString(cursor.getColumnIndex(t.language.getName())));
        e.setYear(cursor.getString(cursor.getColumnIndex(t.year.getName())));
        e.setDetailUrl(cursor.getString(cursor.getColumnIndex(t.detail_url.getName())));
        e.setDownloadUrl(cursor.getString(cursor.getColumnIndex(t.download_url.getName())));
        e.setRemark(cursor.getString(cursor.getColumnIndex(t.remark.getName())));
        e.setCreated(cursor.getString(cursor.getColumnIndex(t.created.getName())));
        e.setUser(cursor.getString(cursor.getColumnIndex(t.user.getName())));
        e.setBookReader(findBookReaderByBookId(e.getId()));
    }

    @SuppressLint("Range")
    private void buildBookReader(Cursor cursor, BookReader e) {
        TableBookReader t = new TableBookReader();
        e.setId(cursor.getString(cursor.getColumnIndex(t.id.getName())));
        e.setBookId(cursor.getString(cursor.getColumnIndex(t.book_id.getName())));
        e.setCur(cursor.getString(cursor.getColumnIndex(t.cur.getName())));
        e.setPages(cursor.getString(cursor.getColumnIndex(t.pages.getName())));
        e.setCreated(cursor.getString(cursor.getColumnIndex(t.created.getName())));
        e.setUser(cursor.getString(cursor.getColumnIndex(t.user.getName())));
        e.setRemark(cursor.getString(cursor.getColumnIndex(t.remark.getName())));
    }

    public void insertViewTime(ViewTime e) {
        try {
            l.lock();
            SQLiteDatabase db = getWritableDatabase();
            TableViewTime t = new TableViewTime();
            ContentValues values = new ContentValues();
            values.put(t.id.getName(), e.getId());
            values.put(t.target_id.getName(), e.getTargetId());
            values.put(t.target_type.getName(), e.getTargetType());
            values.put(t.time.getName(), e.getTime());
            values.put(t.created.getName(), new Date().getTime());
            values.put(t.user.getName(), e.getUser());
            values.put(t.remark.getName(), e.getRemark());
            db.insert(t.getTableName(), null, values);
        } finally {
            l.unlock();
        }
    }

    public List<ViewTime> findViewTime(Date start, String user) {
        try {
            l.lock();
            SQLiteDatabase db = getWritableDatabase();
            TableViewTime t = new TableViewTime();
            String sql = t.selectAll(t.user) + " AND " + t.created + " >= ?";
            String[] params = new String[]{user, start.getTime() + ""};
            LogUtil.d(TAG, "sql:[%s] params:%s", sql, Arrays.toString(params));
            try (Cursor cursor = db.rawQuery(sql, params)) {
                List<ViewTime> list = new ArrayList<>();
                if (cursor.moveToFirst()) {
                    do {
                        ViewTime e = new ViewTime();
                        buildViewTime(cursor, e);
                        list.add(e);
                    } while (cursor.moveToNext());
                }
                return list;
            }
        } finally {
            l.unlock();
        }
    }

    @SuppressLint("Range")
    private void buildViewTime(Cursor cursor, ViewTime e) {
        TableViewTime t = new TableViewTime();
        e.setId(cursor.getLong(cursor.getColumnIndex(t.id.getName())));
        e.setTargetId(cursor.getString(cursor.getColumnIndex(t.target_id.getName())));
        e.setTargetType(cursor.getString(cursor.getColumnIndex(t.target_type.getName())));
        e.setTime(cursor.getLong(cursor.getColumnIndex(t.time.getName())));
        e.setCreated(cursor.getString(cursor.getColumnIndex(t.created.getName())));
        e.setUser(cursor.getString(cursor.getColumnIndex(t.user.getName())));
        e.setRemark(cursor.getString(cursor.getColumnIndex(t.remark.getName())));
    }
}
