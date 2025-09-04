package com.github.jsbxyyx.xbook;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.jsbxyyx.xbook.common.Common;
import com.github.jsbxyyx.xbook.common.DataCallback;
import com.github.jsbxyyx.xbook.common.LogUtil;
import com.github.jsbxyyx.xbook.common.SPUtils;
import com.github.jsbxyyx.xbook.common.UiUtils;
import com.github.jsbxyyx.xbook.data.BookNetHelper;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.util.Arrays;
import java.util.List;

/**
 * @author jsbxyyx
 */
public class SettingsActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private BookNetHelper bookNetHelper;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;
    private String downloadUrl;

    private static String[] clearKeys = new String[]{
            Common.profile_nickname_key,
            Common.profile_email_key,
            Common.search_ext_key,
            Common.search_language_key,
            Common.sync_key,
            Common.reader_image_show_key,
            Common.online_read_key,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final SettingsActivity mActivity = this;

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
            LogUtil.e(TAG, "获取版本失败", e);
            UiUtils.showToast("获取版本失败");
        }

        Button btn_update = findViewById(R.id.btn_update);
        btn_update.setOnClickListener((v) -> {
            if (Common.isEmpty(downloadUrl)) {
                UiUtils.showToast("已经是最新版本");
                LogUtil.d(TAG, "local version is latest.");
            } else {
                UiUtils.showToast("开始下载...");
                LogUtil.d(TAG, "start download apk...");
                Intent intent = new Intent(this, DownloadApkService.class);
                intent.setAction(DownloadApkService.ACTION_START_DOWNLOAD);
                intent.putExtra(DownloadApkService.EXTRA_URL, downloadUrl);
                startService(intent);
            }
        });

        bookNetHelper.cloudVersions(new DataCallback<JsonNode>() {
            @Override
            public void call(JsonNode jsonNode, Throwable err) {
                if (err != null) {
                    LogUtil.d(TAG, "%s", LogUtil.getStackTraceString(err));
                    UiUtils.showToast("获取版本更新失败:" + err.getMessage());
                    return;
                }
                JsonNode data = jsonNode.get("data");
                if (data.isEmpty()) {
                    LogUtil.d(TAG, "versions empty.");
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
                        UiUtils.post(() -> {
                            btn_update.setVisibility(View.VISIBLE);
                            btn_update.setText(cloudName + "");
                        });
                    }
                } catch (Exception e) {
                    LogUtil.e(TAG, "cloud versions exception. %s", LogUtil.getStackTraceString(e));
                    UiUtils.showToast("获取版本更新失败:" + err.getMessage());
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
                    LogUtil.d(TAG, "ext: %s", SPUtils.getData(getBaseContext(), Common.search_ext_key));
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
                    LogUtil.d(TAG, "language: %s", SPUtils.getData(getBaseContext(), Common.search_language_key));
                });
            }
        }

        String sync_data = SPUtils.getData(getBaseContext(), Common.sync_key);
        CheckBox cb_sync = findViewById(R.id.cb_sync);
        if (Common.checked.equals(sync_data)) {
            cb_sync.setChecked(true);
        } else {
            cb_sync.setChecked(false);
        }
        cb_sync.setOnClickListener((v) -> {
            CheckBox cb = (CheckBox) v;
            if (cb.isChecked()) {
                SPUtils.putData(getBaseContext(), Common.sync_key, Common.checked);
            } else {
                SPUtils.putData(getBaseContext(), Common.sync_key, Common.unchecked);
            }
            LogUtil.d(TAG, "sync checked : %s", cb.isChecked());
        });

        String reader_image_show_data = SPUtils.getData(getBaseContext(), Common.reader_image_show_key, Common.checked);
        CheckBox cb_show_image = findViewById(R.id.cb_show_image);
        if (Common.checked.equals(reader_image_show_data)) {
            cb_show_image.setChecked(true);
        } else {
            cb_show_image.setChecked(false);
        }
        cb_show_image.setOnClickListener((v) -> {
            CheckBox cb = (CheckBox) v;
            if (cb.isChecked()) {
                SPUtils.putData(getBaseContext(), Common.reader_image_show_key, Common.checked);
            } else {
                SPUtils.putData(getBaseContext(), Common.reader_image_show_key, Common.unchecked);
            }
            LogUtil.d(TAG, "reader image show checked : %s", cb.isChecked());
        });

        String online_read_data = SPUtils.getData(getBaseContext(), Common.online_read_key, Common.unchecked);
        CheckBox cb_online_read = findViewById(R.id.cb_online_read);
        if (Common.checked.equals(online_read_data)) {
            cb_online_read.setChecked(true);
        } else {
            cb_online_read.setChecked(false);
        }
        cb_online_read.setOnClickListener((v) -> {
            CheckBox cb = (CheckBox) v;
            if (cb.isChecked()) {
                SPUtils.putData(getBaseContext(), Common.online_read_key, Common.checked);
            } else {
                SPUtils.putData(getBaseContext(), Common.online_read_key, Common.unchecked);
            }
            LogUtil.d(TAG, "online read checked : %s", cb.isChecked());
        });

        Button btn_clear_settings = findViewById(R.id.btn_clear_settings);
        btn_clear_settings.setOnClickListener((v) -> {
            for (String key : clearKeys) {
                SPUtils.putData(this, key, "");
            }
            LogUtil.i(TAG, "clear settings : %s", Arrays.toString(clearKeys));
        });

        Button btn_open_write = findViewById(R.id.btn_open_write);
        btn_open_write.setOnClickListener((v) -> {
            try {
                String[] permission = null;
                if (context.getApplicationInfo().targetSdkVersion < 33) {
                    permission = new String[]{
                            Permission.READ_EXTERNAL_STORAGE,
                            Permission.WRITE_EXTERNAL_STORAGE,
                            Permission.MANAGE_EXTERNAL_STORAGE
                    };
                } else {
                    permission = new String[]{
                            Permission.MANAGE_EXTERNAL_STORAGE
                    };
                }
                XXPermissions.with(mActivity)
                        .permission(permission)
                        .request(new OnPermissionCallback() {

                            @Override
                            public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                                if (!allGranted) {
                                    LogUtil.d(TAG, "获取部分权限成功，但部分权限未正常授予");
                                    UiUtils.showToast("获取部分权限成功，但部分权限未正常授予");
                                    return;
                                }
                                LogUtil.d(TAG, "获取管理文件权限成功");
                                UiUtils.showToast("获取管理文件权限成功");
                            }

                            @Override
                            public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                                if (doNotAskAgain) {
                                    LogUtil.d(TAG, "被永久拒绝授权，请手动授予管理文件权限");
                                    UiUtils.showToast("被永久拒绝授权，请手动授予管理文件权限");
                                    // 如果是被永久拒绝就跳转到应用权限系统设置页面
                                    XXPermissions.startPermissionActivity(context, permissions);
                                } else {
                                    LogUtil.d(TAG, "获取管理文件权限失败");
                                    UiUtils.showToast("获取管理文件权限失败");
                                }
                            }
                        });
            } catch (Exception e) {
                LogUtil.e(TAG, LogUtil.getStackTraceString(e));
                UiUtils.showToast("管理文件权限授权失败");
            }
        });
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

}