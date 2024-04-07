package com.github.jsbxyyx.xbook.common;

/**
 * @author jsbxyyx
 * @since 1.0
 */
public class HttpStatusException extends RuntimeException {
    private int statusCode;
    private String url;

    public HttpStatusException(String message, int statusCode, String url) {
        super(message);
        this.statusCode = statusCode;
        this.url = url;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return super.toString() + ". Status=" + statusCode + ", URL=" + url;
    }

}
