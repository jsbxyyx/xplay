package com.github.jsbxyyx.xbook;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.jsbxyyx.xbook.common.Common;
import com.github.jsbxyyx.xbook.common.DataCallback;
import com.github.jsbxyyx.xbook.common.LogUtil;
import com.github.jsbxyyx.xbook.common.ProgressListener;
import com.github.jsbxyyx.xbook.common.SPUtils;
import com.github.jsbxyyx.xbook.data.BookNetHelper;

import java.io.File;

/**
 * @author jsbxyyx
 */
public class SettingsActivity extends AppCompatActivity {

    private BookNetHelper bookNetHelper;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;
    private String downloadUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Context context = getBaseContext();

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "xplay";
            NotificationChannel mChannel = new NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_HIGH);
            mChannel.setSound(null, null);
            mChannel.setImportance(NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(mChannel);
            builder = new NotificationCompat.Builder(this, channelId);
        } else {
            builder = new NotificationCompat.Builder(this);
        }
        builder.setSmallIcon(R.drawable.ic_launcher_foreground)
                .setWhen(System.currentTimeMillis())
                .setContentTitle("xplay APP")
                .setAutoCancel(true)
                .setContentText("版本更新");

        bookNetHelper = new BookNetHelper();

        TextView tv_version = findViewById(R.id.tv_version);
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            tv_version.setText(packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }

        Button btn_update = findViewById(R.id.btn_update);
        btn_update.setOnClickListener((v) -> {
            runOnUiThread(() -> {
                notificationManager.notify(0, builder.build());
                Toast.makeText(context, "开始下载", Toast.LENGTH_LONG).show();
            });
            if (Common.isEmpty(downloadUrl)) {
                Toast.makeText(context, "已经是最新版本", Toast.LENGTH_LONG).show();
            } else {
                bookNetHelper.downloadWithCookie(downloadUrl, Common.sdcard, "", "", new DataCallback<File>() {
                    @Override
                    public void call(File file, Throwable err) {
                        if (err != null) {
                            runOnUiThread(() -> {
                                Toast.makeText(context, "下载失败:" + err.getMessage(), Toast.LENGTH_LONG).show();
                            });
                            return;
                        }
                        LogUtil.d(getClass().getSimpleName(), "下载成功，开始安装");
                        Intent install = new Intent(Intent.ACTION_VIEW);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            install.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider", file);
                            install.setDataAndType(contentUri, "application/vnd.android.package-archive");
                        } else {
                            install.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                        }
                        install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(install);
                    }
                }, new ProgressListener() {
                    @Override
                    public void onProgress(long bytesRead, long total) {
                        runOnUiThread(() -> {
                            int percent = Integer.parseInt(String.format("%.0f", bytesRead * 1.0 / total * 100));
                            if (percent == 100) {
                                notificationManager.cancel(0);
                            } else {
                                builder.setContentInfo(String.valueOf(percent) + "%").setProgress(100, percent, false);
                                notificationManager.notify(0, builder.build());
                            }
                        });
                    }
                });
            }
        });

        bookNetHelper.cloudVersions(new DataCallback<JsonNode>() {
            @Override
            public void call(JsonNode jsonNode, Throwable err) {
                if (err != null) {
                    LogUtil.d(getClass().getSimpleName(), "%s", LogUtil.getStackTraceString(err));
                    runOnUiThread(() -> {
                        Toast.makeText(getBaseContext(), "获取版本更新失败:" + err.getMessage(), Toast.LENGTH_LONG).show();
                    });
                    return;
                }
                JsonNode data = jsonNode.get("data");
                if (data.isEmpty()) {
                    LogUtil.d(getClass().getSimpleName(), "versions empty.");
                    return;
                }
                try {
                    JsonNode update = data.get(0);
                    double localName = Double.parseDouble(tv_version.getText().toString().trim());
                    double cloudName = Double.parseDouble(update.get("name").asText().trim());
                    String body = update.get("body").asText();
                    String published_at = update.get("published_at").asText();
                    String downloadUrl = update.get("assets").get("download_url").asText();
                    String name = update.get("assets").get("name").asText();
                    String size = update.get("assets").get("name").asText();
                    if (cloudName > localName) {
                        setDownloadUrl(downloadUrl);
                        runOnUiThread(() -> {
                            btn_update.setVisibility(View.VISIBLE);
                            btn_update.setText(cloudName + "");
                        });
                    }
                } catch (Exception e) {
                    LogUtil.e(getClass().getSimpleName(), "cloud versions exception. %s", LogUtil.getStackTraceString(e));
                }
            }
        });

        String ext_data = SPUtils.getData(getBaseContext(), Common.search_ext_key);
        LinearLayout ll_ext = findViewById(R.id.ll_ext);
        int ll_ext_count = ll_ext.getChildCount();
        for (int i = 0; i < ll_ext_count; i++) {
            View view = ll_ext.getChildAt(i);
            if (view instanceof CheckBox) {
                String text = ((CheckBox) view).getText().toString();
                if (ext_data.contains(text + Common.comma)) {
                    ((CheckBox) view).setChecked(true);
                }
                view.setOnClickListener((v) -> {
                    CheckBox cb = (CheckBox) v;
                    String text_ = cb.getText().toString();
                    String data_ = SPUtils.getData(getBaseContext(), Common.search_ext_key);
                    if (cb.isChecked()) {
                        if (!data_.contains(text_ + Common.comma)) {
                            data_ += (text_ + Common.comma);
                            SPUtils.putData(getBaseContext(), Common.search_ext_key, data_);
                        }
                    } else {
                        data_ = data_.replace(text_ + Common.comma, "");
                        SPUtils.putData(getBaseContext(), Common.search_ext_key, data_);
                    }
                    LogUtil.d(getClass().getSimpleName(), "ext: %s", SPUtils.getData(getBaseContext(), Common.search_ext_key));
                });
            }
        }

        String language_data = SPUtils.getData(getBaseContext(), Common.search_language_key);
        LinearLayout ll_lang = findViewById(R.id.ll_lang);
        int ll_lang_count = ll_lang.getChildCount();
        for (int i = 0; i < ll_lang_count; i++) {
            View view = ll_lang.getChildAt(i);
            if (view instanceof CheckBox) {
                String text = ((CheckBox) view).getText().toString();
                if (language_data.contains(text + Common.comma)) {
                    ((CheckBox) view).setChecked(true);
                }
                view.setOnClickListener((v) -> {
                    CheckBox cb = (CheckBox) v;
                    String text_ = cb.getText().toString();
                    String data_ = SPUtils.getData(getBaseContext(), Common.search_language_key);
                    if (cb.isChecked()) {
                        if (!data_.contains(text_ + Common.comma)) {
                            data_ += (text_ + Common.comma);
                            SPUtils.putData(getBaseContext(), Common.search_language_key, data_);
                        }
                    } else {
                        data_ = data_.replace(text_ + Common.comma, "");
                        SPUtils.putData(getBaseContext(), Common.search_language_key, data_);
                    }
                    LogUtil.d(getClass().getSimpleName(), "language: %s", SPUtils.getData(getBaseContext(), Common.search_language_key));
                });
            }
        }

        String sync_data = SPUtils.getData(getBaseContext(), Common.sync_key);
        CheckBox cb_sync = findViewById(R.id.cb_sync);
        if (Common.sync_key_checked.equals(sync_data)) {
            cb_sync.setChecked(true);
        } else {
            cb_sync.setChecked(false);
        }
        cb_sync.setOnClickListener((v) -> {
            CheckBox cb = (CheckBox) v;
            if (cb.isChecked()) {
                SPUtils.putData(getBaseContext(), Common.sync_key, Common.sync_key_checked);
            } else {
                SPUtils.putData(getBaseContext(), Common.sync_key, Common.sync_key_unchecked);
            }
            LogUtil.d(getClass().getSimpleName(), "sync checked : %s", cb.isChecked());
        });

    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

}