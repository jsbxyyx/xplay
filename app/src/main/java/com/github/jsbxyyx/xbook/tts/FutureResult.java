package com.github.jsbxyyx.xbook.tts;

public class FutureResult {

    public int code;
    public String message;
    public byte[] output;

    public static FutureResult success(byte[] bytes) {
        FutureResult fr = new FutureResult();
        fr.code = 0;
        fr.message = "OK";
        fr.output = bytes;
        return fr;
    }

    public static FutureResult error(String message) {
        FutureResult fr = new FutureResult();
        fr.code = 1;
        fr.message = message;
        fr.output = new byte[0];
        return fr;
    }
}
