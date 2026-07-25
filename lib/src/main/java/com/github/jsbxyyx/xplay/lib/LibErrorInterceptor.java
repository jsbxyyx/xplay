package com.github.jsbxyyx.xplay.lib;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Mirrors the official Android client's {@code ErrorInterceptor} documented in
 * tmp/api.md: inspects (without consuming) the response body of failed requests
 * and reports errors through {@link LibErrorListener}, so callers can react to
 * expired sessions or surface server-provided error messages, while leaving the
 * response body untouched for normal downstream processing.
 */
public class LibErrorInterceptor implements Interceptor {

    private final ObjectMapper mapper = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    private final LibErrorListener listener;

    public LibErrorInterceptor(@NonNull LibErrorListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        int code = response.code();
        if (code == 401 || code == 403) {
            listener.onUnauthorized();
        } else if (code >= 400 && code < 501) {
            try {
                ResponseBody peeked = response.peekBody(Long.MAX_VALUE);
                JsonNode node = mapper.readTree(peeked.string());
                if (node.has("error")) {
                    listener.onError(code, node.get("error").asText());
                }
            } catch (Exception ignore) {
                // response body is not parsable JSON; ignore, same as the original client
            }
        }
        return response;
    }
}
