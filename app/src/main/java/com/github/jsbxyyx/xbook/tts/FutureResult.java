package com.github.jsbxyyx.xbook.tts;

import java.io.ByteArrayOutputStream;

public class FutureResult {

    public int code;
    public String message;
    public ByteArrayOutputStream output;

    public static FutureResult success(ByteArrayOutputStream output) {
        FutureResult fr = new FutureResult();
        fr.code = 0;
        fr.message = "OK";
        fr.output = output;
        return fr;
    }

    public static FutureResult error(String message) {
        FutureResult fr = new FutureResult();
        fr.code = 1;
        fr.message = message;
        fr.output = new ByteArrayOutputStream();
        return fr;
    }

}
