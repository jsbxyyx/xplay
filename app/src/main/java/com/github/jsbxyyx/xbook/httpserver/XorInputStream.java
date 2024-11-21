package com.github.jsbxyyx.xbook.httpserver;

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
            skip(offset);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long skip(long n) throws IOException {
        return input.skip(n);
    }

    @Override
    public int read() throws IOException {
        return input.read();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int read = input.read(b, off, len);
        if (b != null && b.length > 0 && xor == 1) {
            for (int i = 0; i < len; i++) {
                b[i] = (byte) (b[i] ^ number);
            }
        }
        return read;
    }

}
