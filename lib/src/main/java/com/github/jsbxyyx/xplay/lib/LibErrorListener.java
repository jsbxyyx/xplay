package com.github.jsbxyyx.xplay.lib;

/**
 * Notified about API error responses observed by {@link LibErrorInterceptor}.
 */
public interface LibErrorListener {

    /**
     * Invoked when the server responds with HTTP 401/403, meaning the current
     * session/credentials are no longer valid (mirrors the official client's
     * behavior of dispatching a logout event on these status codes).
     */
    void onUnauthorized();

    /**
     * Invoked for other 4xx/5xx responses whose JSON body carries a parsable
     * {@code error} field.
     */
    void onError(int code, String error);
}
