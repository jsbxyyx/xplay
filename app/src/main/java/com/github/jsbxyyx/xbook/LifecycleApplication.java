package com.github.jsbxyyx.xbook;

import android.app.Application;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.github.jsbxyyx.xbook.common.Common;
import com.github.jsbxyyx.xbook.common.DataCallback;
import com.github.jsbxyyx.xbook.common.LogUtil;
import com.github.jsbxyyx.xbook.common.SPUtils;
import com.github.jsbxyyx.xbook.data.BookNetHelper;
import com.github.jsbxyyx.xbook.data.MLog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

/**
 * @author jsbxyyx
 * @since 1.0
 */
public class LifecycleApplication extends Application {

    private BookNetHelper bookNetHelper;

    @Override
    public void onCreate() {
        super.onCreate();

        bookNetHelper = new BookNetHelper();

        String languages = "chinese,japanese,traditional chinese,english,korean,";
        SPUtils.putData(getBaseContext(), Common.search_language_key, languages);

        String data = SPUtils.getData(getBaseContext(), Common.search_ext_key);
        if (TextUtils.isEmpty(data)) {
            data = "EPUB,";
            SPUtils.putData(getBaseContext(), Common.search_ext_key, data);
        }

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
                MLog mLog = new MLog();
                mLog.setTitle(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + Common.log_suffix);
                mLog.setRaw(LogUtil.getStackTraceString(e));
                CountDownLatch latch = new CountDownLatch(1);
                bookNetHelper.cloudLog(mLog, new DataCallback() {
                    @Override
                    public void call(Object o, Throwable err) {
                        try {
                            if (err != null) {
                                LogUtil.e(getClass().getSimpleName(), "cloud log err. %s", LogUtil.getStackTraceString(err));
                            } else {
                                new Thread(() -> {
                                    Looper.prepare();
                                    Toast.makeText(getBaseContext(), "闪退异常上报成功", Toast.LENGTH_LONG).show();
                                    Looper.loop();
                                }).start();
                                Thread.sleep(2000);
                            }
                        } catch (InterruptedException ignore) {
                        } finally {
                            latch.countDown();
                        }
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    }
                });
                try {
                    latch.await();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        });

    }
}
