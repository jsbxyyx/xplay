package com.github.jsbxyyx.xbook.data;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * @author jsbxyyx
 */
public class HttpHelper {

    private static OkHttpClient client = null;

    private static OkHttpClient syncClient = null;

    public static final OkHttpClient getClient() {
        if (client != null) {
            return client;
        }
        synchronized (HttpHelper.class) {
            if (client == null) {
                client = new OkHttpClient.Builder()
                        .connectTimeout(60000, TimeUnit.MILLISECONDS)
                        .readTimeout(300000, TimeUnit.MILLISECONDS)
                        .writeTimeout(300000, TimeUnit.MILLISECONDS)
                        .build();
            }
            return client;
        }
    }

    public static final OkHttpClient getSyncClient() {
        if (syncClient != null) {
            return syncClient;
        }
        synchronized (HttpHelper.class) {
            if (syncClient == null) {
                syncClient = new OkHttpClient.Builder()
                        .connectTimeout(60000, TimeUnit.MILLISECONDS)
                        .readTimeout(3600000, TimeUnit.MILLISECONDS)
                        .writeTimeout(3600000, TimeUnit.MILLISECONDS)
                        .build();
            }
            return syncClient;
        }
    }

}
