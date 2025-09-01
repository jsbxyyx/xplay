package com.github.jsbxyyx.xbook;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class DownloadBookService extends Service {

    private final String TAG = getClass().getSimpleName();
    public static final String ACTION_START_DOWNLOAD = "com.github.jsbxyyx.xbook.action.START_BOOK_DOWNLOAD";
    public static final String EXTRA_URL = "download_url";
    public static final String EXTRA_DIR = "download_dir";
    public static final String EXTRA_UID = "download_uid";
    public static final String EXTRA_TYPE = "download_type";
    public static final String EXTRA_BOOK = "download_book";

    public static final String CHANNEL_ID = "download_book_channel";
    private static final int FOREGROUND_NOTIFICATION_ID = 1000;
    private NotificationManager notificationManager;
    private final ConcurrentHashMap<String, DownloadTask> downloadTasks = new ConcurrentHashMap<>();
    private final AtomicInteger notificationIdGenerator = new AtomicInteger(FOREGROUND_NOTIFICATION_ID + 1);
    private volatile boolean isForegroundStarted = false;

    private BookNetHelper bookNetHelper;
    private BookDbHelper bookDbHelper;

    public DownloadBookService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        bookNetHelper = new BookNetHelper();
        bookDbHelper = BookDbHelper.getInstance();

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_START_DOWNLOAD.equals(intent.getAction())) {
            final String url = intent.getStringExtra(EXTRA_URL);
            final String dir = intent.getStringExtra(EXTRA_DIR);
            final String uid = intent.getStringExtra(EXTRA_UID);
            final String type = intent.getStringExtra(EXTRA_TYPE);
            final String bookJSON = intent.getStringExtra(EXTRA_BOOK);
            if (url != null && dir != null && bookJSON != null) {
                startDownload(url, dir, uid, type, bookJSON);
            }
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (DownloadTask task : downloadTasks.values()) {
            notificationManager.cancel(task.notificationId);
        }
        downloadTasks.clear();
        isForegroundStarted = false;
    }

    private void startDownload(String url, String dir, String uid, String type, String bookJSON) {
        Book mBook = JsonUtil.fromJson(bookJSON, Book.class);

        String title = mBook.getTitle();
        String downloadId = url.hashCode() + "";
        int notificationId = notificationIdGenerator.getAndIncrement();

        if (downloadTasks.containsKey(downloadId)) {
            LogUtil.w(TAG, "文件已在下载中: %s" + title);
            return;
        }
        DownloadTask task = new DownloadTask(downloadId, notificationId, System.currentTimeMillis(), title, Common.newMap(
                "url", url,
                "dir", dir,
                "uid", uid,
                "type", type,
                "book", mBook
        ));
        downloadTasks.put(downloadId, task);

        Notification notification = createDownloadNotification(task.title, 0, "开始下载...", notificationId);
        // 第一个下载任务启动前台服务，使用下载通知
        if (!isForegroundStarted) {
            startForeground(notificationId, notification);
            isForegroundStarted = true;
        } else {
            notificationManager.notify(notificationId, notification);
        }
        new Thread(() -> downloadBook(task)).start();
    }

    private void downloadBook(DownloadTask task) {
        Map<String, Object> params = task.params;
        String url = (String) params.get("url");
        String dir = (String) params.get("dir");
        String uid = (String) params.getOrDefault("uid", "");
        String type = (String) params.get("type");
        Book mBook = (Book) params.get("book");
        bookNetHelper.downloadWithMagicSync(url, dir, uid, new DataCallback<File>() {
            @Override
            public void call(File file, Throwable err) {
                if (err != null) {
                    UiUtils.showToast("书籍下载失败:" + err.getMessage());

                    downloadTasks.remove(task.downloadId);
                    updateDownloadNotification(task.title, 0, "下载失败", task.notificationId);
                    if (downloadTasks.isEmpty()) {
                        stopForeground(true);
                        stopSelf();
                    }
                    return;
                }
                UiUtils.showToast("下载成功");
                LogUtil.d(TAG, "call: file: %s : %s", file.getAbsolutePath(), file.length());

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
                downloadTasks.remove(task.downloadId);
                updateDownloadNotification(task.title, 100, "下载完成", task.notificationId);
                if (downloadTasks.isEmpty()) {
                    stopForeground(true);
                    stopSelf();
                }
            }
        }, (bytesRead, total) -> {
            int progress = (int) (bytesRead * 1.0 / total * 100);
            long currentTime = System.currentTimeMillis();
            if (currentTime - task.lastUpdateTime > 1000) {
                task.lastUpdateTime = currentTime;
                updateDownloadNotification(task.title, progress, "下载中...", task.notificationId);
            }
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

    private Notification createForegroundNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ?
                        PendingIntent.FLAG_IMMUTABLE : PendingIntent.FLAG_UPDATE_CURRENT
        );
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle("下载服务")
                .setContentText("下载服务运行中")
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
    }

    private Notification createDownloadNotification(String title, int progress, String status, int notificationId) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, notificationId, intent,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ?
                        PendingIntent.FLAG_IMMUTABLE : PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(this);
        }
        builder.setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle("下载文件")
                .setContentText(title + " - " + status)
                .setContentIntent(pendingIntent);

        if (status.contains("下载中") || status.contains("开始下载")) {
            builder.setOngoing(true)  // 下载中时设置为持续通知，不能滑动删除
                    .setAutoCancel(false);
            if (progress > 0) {
                builder.setProgress(100, progress, false)
                        .setSubText(progress + "%");
            } else {
                builder.setProgress(100, 0, true);
            }
        } else {
            builder.setOngoing(false)
                    .setAutoCancel(true);
            if (status.contains("下载完成")) {
                builder.setSmallIcon(android.R.drawable.stat_sys_download_done)
                        .setProgress(100, 100, false)
                        .setSubText("100%");
            } else if (status.contains("下载失败")) {
                builder.setSmallIcon(android.R.drawable.stat_notify_error)
                        .setProgress(0, 0, false);
            }
        }

        return builder.build();
    }

    private void updateDownloadNotification(String title, int progress, String status, int notificationId) {
        Notification notification = createDownloadNotification(title, progress, status, notificationId);
        notificationManager.notify(notificationId, notification);
    }

    private static class DownloadTask {
        public final String downloadId;
        public final int notificationId;
        public long lastUpdateTime;
        public final String title;
        public final Map<String, Object> params;

        public DownloadTask(String downloadId, int notificationId, long lastUpdateTime, String title, Map<String, Object> params) {
            this.downloadId = downloadId;
            this.notificationId = notificationId;
            this.lastUpdateTime = lastUpdateTime;
            this.title = title;
            this.params = params;
        }
    }

}