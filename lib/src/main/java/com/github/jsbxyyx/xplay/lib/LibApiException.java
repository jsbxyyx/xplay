package com.github.jsbxyyx.xplay.lib;

import androidx.annotation.Nullable;

import java.io.IOException;

/**
 * Lib API request failure.
 */
public class LibApiException extends IOException {

    private final int code;
    @Nullable
    private final String responseBody;

    public LibApiException(int code, @Nullable String responseBody) {
        super("Lib API request failed with HTTP " + code);
        this.code = code;
        this.responseBody = responseBody;
    }

    public LibApiException(int code, @Nullable String responseBody, Throwable cause) {
        super("Lib API request failed with HTTP " + code, cause);
        this.code = code;
        this.responseBody = responseBody;
    }

    public int getCode() {
        return code;
    }

    @Nullable
    public String getResponseBody() {
        return responseBody;
    }
}
