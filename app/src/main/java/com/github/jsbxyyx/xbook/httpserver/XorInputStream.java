package com.github.jsbxyyx.xbook.httpserver;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class XorInputStream extends InputStream {

    private InputStream input;
    private int offset;
    private byte number;
    private int xor;

    public XorInputStream(InputStream input, int offset, byte number, int xor) {
        this.input = input;
        this.offset = offset;
        this.number = number;
        this.xor = xor;
        try {
            input.skip(offset);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long skip(long n) throws IOException {
        return input.skip(offset + n);
    }

    @Override
    public int read() throws IOException {
        int read = input.read();
        if (read != -1) {
            int fRead = xor == 1 ? read ^ number : read;
            return fRead;
        } else {
            return read;
        }
    }

}
