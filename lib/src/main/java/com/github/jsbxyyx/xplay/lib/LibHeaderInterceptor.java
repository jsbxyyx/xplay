package com.github.jsbxyyx.xplay.lib;

import android.os.Build;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Adds the common headers required by the z-library mobile API, mirroring the
 * official Android client's {@code HeaderInterceptor} documented in tmp/api.md.
 * <p>
 * Every request receives the {@code Authorization}/{@code source}/device headers,
 * and when a request already carries {@code remix-userid}/{@code remix-userkey}
 * headers (see {@link LibApiClient.Auth}), an equivalent {@code Cookie} header is
 * added as well, since some endpoints only honor the cookie form of the credentials.
 */
public class LibHeaderInterceptor implements Interceptor {

    public static final String AUTHORIZATION_VALUE = "aMaaSIA86Uq8mOzt";
    public static final String HEADER_REMIX_USER_ID = "remix-userid";
    public static final String HEADER_REMIX_USER_KEY = "remix-userkey";

    private final String appVersion;
    private final String language;

    public LibHeaderInterceptor() {
        this("1.11.4", "en");
    }

    public LibHeaderInterceptor(@NonNull String appVersion, @NonNull String language) {
        this.appVersion = appVersion;
        this.language = language;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder newBuilder = request.newBuilder()
                .header("Authorization", AUTHORIZATION_VALUE)
                .header("source", "android")
                .header("ANDROID-APP-LANGUAGE", language)
                .header("ANDROID-APP-VERSION", appVersion)
                .header("appversion", appVersion)
                .header("ANDROID-OS-VERSION", Build.VERSION.RELEASE == null ? "" : Build.VERSION.RELEASE)
                .header("ANDROID-MOBILE-VERSION", Build.MODEL == null ? "" : Build.MODEL);

        String userId = request.header(HEADER_REMIX_USER_ID);
        String userKey = request.header(HEADER_REMIX_USER_KEY);
        if (userId != null && userKey != null) {
            newBuilder.header("Cookie", "remix_userId=" + userId + "; remix_userkey=" + userKey);
        }
        return chain.proceed(newBuilder.build());
    }
}
