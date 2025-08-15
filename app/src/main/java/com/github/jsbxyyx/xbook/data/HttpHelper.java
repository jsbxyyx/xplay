package com.github.jsbxyyx.xbook.data;

import com.github.jsbxyyx.xbook.common.Common;
import com.github.jsbxyyx.xbook.common.LogUtil;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Dns;
import okhttp3.OkHttpClient;

/**
 * @author jsbxyyx
 */
public class HttpHelper {

    private static OkHttpClient dnsClient = null;
    private static OkHttpClient client = null;

    private static OkHttpClient syncClient = null;

    private static CustomDns customDns = new CustomDns();

    public static OkHttpClient getDnsClient() {
        if (dnsClient != null) {
            return dnsClient;
        }
        synchronized (HttpHelper.class) {
            if (dnsClient == null) {
                dnsClient = new OkHttpClient.Builder()
                        .connectTimeout(45000, TimeUnit.MILLISECONDS)
                        .readTimeout(120000, TimeUnit.MILLISECONDS)
                        .writeTimeout(120000, TimeUnit.MILLISECONDS)
                        .build();
            }
            return dnsClient;
        }
    }

    public static OkHttpClient getClient() {
        if (client != null) {
            return client;
        }
        synchronized (HttpHelper.class) {
            if (client == null) {
                client = new OkHttpClient.Builder()
                        .connectTimeout(45000, TimeUnit.MILLISECONDS)
                        .readTimeout(120000, TimeUnit.MILLISECONDS)
                        .writeTimeout(120000, TimeUnit.MILLISECONDS)
                        .dns(customDns)
                        .build();
            }
            return client;
        }
    }

    public static OkHttpClient getSyncClient() {
        if (syncClient != null) {
            return syncClient;
        }
        synchronized (HttpHelper.class) {
            if (syncClient == null) {
                syncClient = new OkHttpClient.Builder()
                        .connectTimeout(60000, TimeUnit.MILLISECONDS)
                        .readTimeout(3600000, TimeUnit.MILLISECONDS)
                        .writeTimeout(3600000, TimeUnit.MILLISECONDS)
                        .dns(customDns)
                        .build();
            }
            return syncClient;
        }
    }

    public static class CustomDns implements Dns {

        @Override
        public List<InetAddress> lookup(String s) throws UnknownHostException {
            if (!Common.isEmpty(Common.getIPS()) && (
                    Common.host.equals(s) || Common.tts_host.equals(s)
            )) {
                List<InetAddress> addresses = new ArrayList<>();
                String ip = Common.getIp();
                LogUtil.d("dns-lookup", "%s", ip);
                addresses.add(InetAddress.getByName(ip));
                return addresses;
            }
            return SYSTEM.lookup(s);
        }
    }

}
