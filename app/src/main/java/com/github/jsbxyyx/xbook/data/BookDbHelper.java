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
        LogUtil.i(TAG, "SQL：[%s]", s1);
        db.execSQL(s1);

        TableBookReader t2 = new TableBookReader();
        String s2 = t2.create(false, true, "");
        LogUtil.i(TAG, "sql:[%s]", s2);
        db.execSQL(s2);

        TableViewTime t3 = new TableViewTime();
        String s3 = t3.create(false, true, "");
        LogUtil.i(TAG, "sql:[%s]", s3);
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
                String backup_create_sql = t.create(true, false, suffix);
                String backup_table_name = t.getTableName() + suffix;
                String origin_create_sql = t.create(false, false, "");
                String origin_table_name = t.getTableName();

                LogUtil.i(TAG, "sql:[%s]", backup_create_sql);
                db.execSQL(backup_create_sql);
                String insert_backup_sql = "INSERT INTO " + backup_table_name + " SELECT " + t.getAllFieldString() + " FROM " + origin_table_name;
                LogUtil.i(TAG, "sql:[%s]", insert_backup_sql);
                db.execSQL(insert_backup_sql);
                String drop_origin_sql = "DROP TABLE " + origin_table_name;
                LogUtil.i(TAG, "sql:[%s]", drop_origin_sql);
                db.execSQL(drop_origin_sql);
                LogUtil.i(TAG, "sql:[%s]", origin_create_sql);
                db.execSQL(origin_create_sql);
                String insert_origin_sql = "INSERT INTO " + origin_table_name + " SELECT " + t.getAllFieldString() + " FROM " + backup_table_name;
                LogUtil.i(TAG, "sql:[%s]", insert_origin_sql);
                db.execSQL(insert_origin_sql);
                String drop_backup_sql = "DROP TABLE " + backup_table_name;
                LogUtil.i(TAG, "sql:[%s]", drop_backup_sql);
                db.execSQL(drop_backup_sql);

                db.setTransactionSuccessful();
            } catch (Exception e) {
                LogUtil.e(TAG, LogUtil.getStackTraceString(e));
                UiUtils.showToast("升级失败，请卸载重新安装");
            } finally {
                db.endTransaction();
            }
        }
    }

    public void insertBook(Book e) {
        try {
            l.lock();
            SQLiteDatabase db = getWritableDatabase();

            TableBook t = new TableBook();
            String sql = t.insert();
            Object[] args = new Object[]{
                    e.getId(), e.getBid(), e.getIsbn(),
                    e.getCoverImage(), e.getTitle(), e.getPublisher(),
                    e.getAuthors(), e.getFile(), e.getLanguage(),
                    e.getYear(), e.getDetailUrl(), e.getDownloadUrl(),
                    e.getRemark(), new Date().getTime(), e.getUser()
            };
            LogUtil.d(TAG, "sql:[%s] args:%s", sql, Arrays.toString(args));
            db.execSQL(sql, args);
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
            String[] args = new String[]{bid};
            LogUtil.d(TAG, "sql:[%s] args:%s", sql, Arrays.toString(args));
            try (Cursor cursor = db.rawQuery(sql, args)) {
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
            String[] args = new String[]{id};
            LogUtil.d(TAG, "sql:[%s] args:%s", sql, Arrays.toString(args));
            try (Cursor cursor = db.rawQuery(sql, args)) {
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
            String[] args = new String[]{};
            LogUtil.d(TAG, "sql:[%s] args:%s", sql, Arrays.toString(args));
            try (Cursor cursor = db.rawQuery(sql, args)) {
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
            String[] args = new String[]{bookId + ""};
            LogUtil.d(TAG, "sql:[%s] args:%s", sql, Arrays.toString(args));
            try (Cursor cursor = db.rawQuery(sql, args)) {
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
        SQLiteDatabase db = getWritableDatabase();
        try {
            l.lock();

            db.beginTransactionNonExclusive();

            TableBook t1 = new TableBook();
            String sql1 = t1.delete(t1.id);
            Object[] args1 = new Object[]{id};
            LogUtil.d(TAG, "sql:[%s] args:%s", sql1, Arrays.toString(args1));
            db.execSQL(sql1, args1);

            TableBookReader t2 = new TableBookReader();
            String sql2 = t2.delete(t2.book_id);
            Object[] args2 = new Object[]{id};
            LogUtil.d(TAG, "sql:[%s] args:%s", sql2, Arrays.toString(args2));
            db.execSQL(sql2, args2);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            l.unlock();
        }
    }

    public void updateBook(Book book) {
        try {
            l.lock();
            SQLiteDatabase db = getWritableDatabase();
            TableBook t = new TableBook();
            String sql = t.update(t.getAllField(t.id).toArray(new TableField[0]), t.id);
            Object[] args = new Object[]{
                    book.getBid(), book.getIsbn(), book.getCoverImage(), book.getTitle(),
                    book.getPublisher(), book.getAuthors(), book.getFile(), book.getLanguage(),
                    book.getYear(), book.getDetailUrl(), book.getDownloadUrl(), book.getRemark(),
                    book.getCreated(), book.getUser(),
                    book.getId()
            };
            LogUtil.d(TAG, "sql:[%s] args:%s", sql, Arrays.toString(args));
            db.execSQL(sql, args);
        } finally {
            l.unlock();
        }
    }

    public void insertBookReader(BookReader e) {
        try {
            l.lock();
            SQLiteDatabase db = getWritableDatabase();

            TableBookReader t = new TableBookReader();
            String sql = t.insert();
            Object[] args = new Object[]{
                    e.getId(), e.getBookId(), e.getCur(),
                    e.getPages(), new Date().getTime(), e.getUser(),
                    e.getRemark()
            };
            LogUtil.d(TAG, "sql:[%s] args:%s", sql, Arrays.toString(args));
            db.execSQL(sql, args);
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
            Object[] args = new Object[]{
                    e.getCur(), e.getPages(), e.getRemark(),
                    e.getBookId()
            };
            LogUtil.d(TAG, "sql:[%s] args:%s", sql, Arrays.toString(args));
            db.execSQL(sql, args);
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

            String sql = t.insert();
            Object[] args = new Object[]{
                    e.getId(), e.getTargetId(), e.getTargetType(),
                    e.getTime(), new Date().getTime(), e.getUser(),
                    e.getRemark()
            };
            LogUtil.d(TAG, "sql:[%s] args:%s", sql, Arrays.toString(args));
            db.execSQL(sql, args);
        } finally {
            l.unlock();
        }
    }

    public List<ViewTime> findViewTime(Date start, String user) {
        try {
            l.lock();
            SQLiteDatabase db = getWritableDatabase();
            TableViewTime t = new TableViewTime();
            String sql = t.selectAll(t.user) + " AND " + t.created.getName() + ">=?";
            String[] args = new String[]{user, start.getTime() + ""};
            LogUtil.d(TAG, "sql:[%s] args:%s", sql, Arrays.toString(args));
            try (Cursor cursor = db.rawQuery(sql, args)) {
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
