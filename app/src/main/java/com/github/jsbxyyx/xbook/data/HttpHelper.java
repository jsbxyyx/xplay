package com.github.jsbxyyx.xbook.data;

import androidx.annotation.NonNull;

import com.github.jsbxyyx.xbook.common.Common;
import com.github.jsbxyyx.xbook.common.CookieUtils;
import com.github.jsbxyyx.xbook.common.LogUtil;
import com.github.jsbxyyx.xbook.common.SPUtils;
import com.github.jsbxyyx.xbook.common.SeedGenerator;
import com.github.jsbxyyx.xbook.common.SessionManager;
import com.github.jsbxyyx.xbook.common.UriUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Dns;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

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
                        .connectTimeout(5000, TimeUnit.MILLISECONDS)
                        .readTimeout(30000, TimeUnit.MILLISECONDS)
                        .writeTimeout(30000, TimeUnit.MILLISECONDS)
                        .dns(customDns)
                        .addInterceptor(new ChallengeInterceptor())
                        .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
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
                        .connectTimeout(5000, TimeUnit.MILLISECONDS)
                        .readTimeout(3600000, TimeUnit.MILLISECONDS)
                        .writeTimeout(3600000, TimeUnit.MILLISECONDS)
                        .dns(customDns)
                        .addInterceptor(new ChallengeInterceptor())
                        .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
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
//                return addresses;
            }
            return SYSTEM.lookup(s);
        }
    }

    private static class ChallengeInterceptor implements Interceptor {

        @NonNull
        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {
            Request request = chain.request();
            Response response = chain.proceed(request);
            try {
                if (!response.isSuccessful()) {
                    String challenge = response.header("X-challenge");
                    if (!Common.isBlank(challenge)) {
                        String challengeHtml = UriUtils.decodeURIComponent(challenge);

                        Map<String, Object> challengeMap = SeedGenerator.generate(SeedGenerator.extractSeed(challengeHtml));
                        String respCookie = SeedGenerator.extractCookie(challengeHtml);
                        String c_token = (String) challengeMap.get("c_token");;
                        String c_time = (String) challengeMap.get("c_time");

                        String login = SPUtils.getData(Common.login_key);
                        String newCookie = login;
                        if (Common.isBlank(login)) {
                            newCookie = "";
                        }
                        newCookie = CookieUtils.putAll(newCookie, CookieUtils.parse(respCookie));
                        newCookie = CookieUtils.put(newCookie, "c_token", c_token);
                        newCookie = CookieUtils.put(newCookie, "c_time", c_time);
                        newCookie = CookieUtils.put(newCookie, "siteLanguage", "en");
                        SessionManager.setSession(SPUtils.getData(Common.login_key));
                        LogUtil.i("HttpHelper", "newCookie:%s", newCookie);
                        response.close();
                        Request retryRequest = request.newBuilder().header("cookie", newCookie).build();
                        LogUtil.w("HttpHelper", "http try again: %s", request.url().encodedPath());
                        return chain.proceed(retryRequest);
                    }
                }
            } catch (Exception e) {
                LogUtil.e("HttpHelper", "%s", LogUtil.getStackTraceString(e));
            }
            return response;
        }
    }

}
