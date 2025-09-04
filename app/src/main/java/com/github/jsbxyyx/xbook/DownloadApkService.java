package com.github.jsbxyyx.xbook;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import com.github.jsbxyyx.xbook.common.Common;
import com.github.jsbxyyx.xbook.common.DataCallback;
import com.github.jsbxyyx.xbook.common.LogUtil;
import com.github.jsbxyyx.xbook.common.UiUtils;
import com.github.jsbxyyx.xbook.data.BookNetHelper;

import java.io.File;
import java.util.List;

public class DownloadApkService extends Service {

    private final String TAG = getClass().getSimpleName();
    public static final String ACTION_START_DOWNLOAD = "com.github.jsbxyyx.xbook.action.START_APK_DOWNLOAD";
    public static final String EXTRA_URL = "download_url";
    public static final String CHANNEL_ID = "download_apk_channel";
    public static final int NOTIFY_ID = 2001;

    private volatile boolean isDownloading = false;

    private BookNetHelper bookNetHelper;

    public DownloadApkService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        bookNetHelper = new BookNetHelper();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_START_DOWNLOAD.equals(intent.getAction())) {
            final String url = intent.getStringExtra(EXTRA_URL);
            if (!isDownloading && url != null) {
                isDownloading = true;
                startForeground(NOTIFY_ID, buildNotification("下载开始...", 0));
                new Thread(() -> downloadApk(url)).start();
            }
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void downloadApk(String url) {
        Context context = this;
        bookNetHelper.downloadApk(url, new DataCallback<File>() {
            @Override
            public void call(File file, Throwable err) {
                if (err != null) {
                    LogUtil.d(TAG, "download failed. %s", LogUtil.getStackTraceString(err));
                    UiUtils.showToast("下载失败:" + err.getMessage());
                    stopForeground(true);
                    isDownloading = false;
                    stopSelf();
                    return;
                }
                UiUtils.showToast("下载成功，开始安装");
                LogUtil.d(TAG, "download success. start install apk");
                Common.sleep(3000);
                Intent install = new Intent(Intent.ACTION_VIEW);
                install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri uri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    uri = FileProvider.getUriForFile(
                            context,
                            context.getPackageName() + ".fileProvider",
                            file
                    );
                    install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    install.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                } else {
                    uri = Uri.fromFile(file);
                }
                install.setDataAndType(uri, "application/vnd.android.package-archive");
                List<ResolveInfo> resInfoList = context.getPackageManager()
                        .queryIntentActivities(
                                install,
                                PackageManager.MATCH_DEFAULT_ONLY
                        );
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    int modeFlags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION;
                    getApplicationContext().grantUriPermission(
                            packageName,
                            uri,
                            modeFlags
                    );
                }
                getApplicationContext().startActivity(install);

                stopForeground(false);
                showFinishNotification(file.getName(), file.getAbsolutePath());
                isDownloading = false;
                stopSelf();
            }
        }, (bytesRead, total) -> {
            int progress = (int)(bytesRead * 1.0 / total * 100);
            updateNotification("正在下载..." + progress + "%", progress);
        }, true);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "APK下载",
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
        builder.setContentTitle("版本更新下载")
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