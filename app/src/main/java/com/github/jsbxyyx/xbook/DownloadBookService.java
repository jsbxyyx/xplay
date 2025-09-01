package com.github.jsbxyyx.xbook;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.jsbxyyx.xbook.common.Common;
import com.github.jsbxyyx.xbook.common.DataCallback;
import com.github.jsbxyyx.xbook.common.IdUtil;
import com.github.jsbxyyx.xbook.common.JsonUtil;
import com.github.jsbxyyx.xbook.common.LogUtil;
import com.github.jsbxyyx.xbook.common.SPUtils;
import com.github.jsbxyyx.xbook.common.UiUtils;
import com.github.jsbxyyx.xbook.data.BookDbHelper;
import com.github.jsbxyyx.xbook.data.BookNetHelper;
import com.github.jsbxyyx.xbook.data.bean.Book;

import java.io.File;

public class DownloadBookService extends Service {

    private final String TAG = getClass().getSimpleName();
    public static final String ACTION_START_DOWNLOAD = "com.github.jsbxyyx.xbook.action.START_BOOK_DOWNLOAD";
    public static final String EXTRA_URL = "download_url";
    public static final String EXTRA_DIR = "download_dir";
    public static final String EXTRA_UID = "download_uid";
    public static final String EXTRA_TYPE = "download_type";
    public static final String EXTRA_BOOK = "download_book";
    public static final String CHANNEL_ID = "download_book_channel";
    public static final int NOTIFY_ID = 1001;

    private volatile boolean isDownloading = false;

    private BookNetHelper bookNetHelper;
    private BookDbHelper bookDbHelper;

    public DownloadBookService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        bookNetHelper = new BookNetHelper();
        bookDbHelper = BookDbHelper.getInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_START_DOWNLOAD.equals(intent.getAction())) {
            final String url = intent.getStringExtra(EXTRA_URL);
            final String dir = intent.getStringExtra(EXTRA_DIR);
            final String uid = intent.getStringExtra(EXTRA_UID);
            final String type = intent.getStringExtra(EXTRA_TYPE);
            final String bookJSON = intent.getStringExtra(EXTRA_BOOK);
            if (!isDownloading && url != null && dir != null) {
                isDownloading = true;
                startForeground(NOTIFY_ID, buildNotification("下载开始...", 0));
                new Thread(() -> downloadBook(url, dir, uid, type, bookJSON)).start();
            }
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void downloadBook(String url, String dir, String uid, String type, String bookJSON) {
        bookNetHelper.downloadWithMagicSync(url, dir, uid, new DataCallback<File>() {
            @Override
            public void call(File file, Throwable err) {
                if (err != null) {
                    UiUtils.showToast("书籍下载失败:" + err.getMessage());
                    updateNotification("下载失败", 0);
                    stopForeground(true);
                    isDownloading = false;
                    stopSelf();
                    return;
                }
                updateNotification("下载完成", 100);
                LogUtil.d(TAG, "call: file: %s : %s", file.getAbsolutePath(), file.length());

                Book mBook = JsonUtil.fromJson(bookJSON, Book.class);
                if (Common.downloaded.equalsIgnoreCase(type)) {
                    String file_path = mBook.extractFilePath();
                    if (!Common.isBlank(file_path) && !file_path.equals(file.getAbsolutePath())) {
                        mBook.fillFilePath(file.getAbsolutePath());
                        bookDbHelper.updateBook(mBook);
                        LogUtil.i(TAG, "update " + mBook.getBid() + "/" + mBook.getTitle() + " file path.");
                    }
                } else if (Common.not_downloaded.equals(type)) {
                    mBook.fillFilePath(file.getAbsolutePath());
                    mBook.setUser(SPUtils.getData(getBaseContext(), Common.profile_email_key));
                    Book by = bookDbHelper.findBookByBid(mBook.getBid());
                    if (by == null) {
                        mBook.setId(IdUtil.nextId());
                        bookDbHelper.insertBook(mBook);
                        String sync_data = SPUtils.getData(getBaseContext(), Common.sync_key);
                        if (Common.checked.equals(sync_data)) {
                            bookNetHelper.cloudSync(bookDbHelper.findBookByBid(mBook.getBid()), true, new DataCallback<JsonNode>() {
                                @Override
                                public void call(JsonNode o, Throwable err) {
                                    if (err != null) {
                                        UiUtils.showToast("同步失败:" + err.getMessage());
                                        return;
                                    }
                                    String sha = o.get("data").get("sha").asText();
                                    Book book_db = bookDbHelper.findBookById(mBook.getId() + "");
                                    if (book_db != null) {
                                        book_db.fillSha(sha);
                                        bookDbHelper.updateBook(book_db);
                                    }
                                    if (book_db != null) {
                                        UiUtils.showToast("同步成功");
                                    }
                                }
                            });
                        }
                    }
                } else {
                    UiUtils.showToast("不支持的类型:" + type);
                }
                UiUtils.showToast("下载成功");
                stopForeground(false);
                showFinishNotification(mBook.getTitle(), file.getAbsolutePath());
                isDownloading = false;
                stopSelf();
            }
        }, (bytesRead, total) -> {
            int progress = (int)(bytesRead * 1.0 / total * 100);
            updateNotification("正在下载..." + progress + "%", progress);
        }, Common.MAGIC);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "文件下载",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("下载文件时显示进度");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private Notification buildNotification(String text, int progress) {
        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(this);
        }
        builder.setContentTitle("下载服务")
                .setContentText(text)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOnlyAlertOnce(true)
                .setProgress(100, progress, false);
        return builder.build();
    }

    private void updateNotification(String text, int progress) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = buildNotification(text, progress);
        manager.notify(NOTIFY_ID, notification);
    }

    private void showFinishNotification(String filename, String filePath) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("下载完成")
                .setContentText(filename)
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(NOTIFY_ID + 1, builder.build());
    }


}