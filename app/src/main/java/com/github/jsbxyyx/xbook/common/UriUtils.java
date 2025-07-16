package com.github.jsbxyyx.xbook.common;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author jsbxyyx
 */
public class UriUtils {

    public static String encodeURIComponent(String source) {
        if (source == null || source.trim().isEmpty()) {
            return "";
        }

        byte[] bytes = source.getBytes(StandardCharsets.UTF_8);
        boolean original = true;
        for (byte b : bytes) {
            if (!(
                    (b >= 'a' && b <= 'z' || b >= 'A' && b <= 'Z')
                            || (b >= '0' && b <= '9')
                            || '-' == b || '.' == b || '_' == b || '~' == b
            )) {
                original = false;
                break;
            }
        }
        if (original) {
            return source;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length);
        for (byte b : bytes) {
            if (
                    (b >= 'a' && b <= 'z' || b >= 'A' && b <= 'Z')
                            || (b >= '0' && b <= '9')
                            || '-' == b || '.' == b || '_' == b || '~' == b
            ) {
                baos.write(b);
            } else {
                baos.write('%');
                char hex1 = Character.toUpperCase(Character.forDigit((b >> 4) & 0xF, 16));
                char hex2 = Character.toUpperCase(Character.forDigit(b & 0xF, 16));
                baos.write(hex1);
                baos.write(hex2);
            }
        }
        return new String(baos.toByteArray(), StandardCharsets.UTF_8);
    }

    public static String decodeURIComponent(String source) {
        if (source == null || source.trim().isEmpty()) {
            return "";
        }
        int length = source.length();
        ByteArrayOutputStream baos = new ByteArrayOutputStream(length);
        boolean changed = false;
        for (int i = 0; i < length; i++) {
            int ch = source.charAt(i);
            if (ch == '%') {
                if (i + 2 < length) {
                    char hex1 = source.charAt(i + 1);
                    char hex2 = source.charAt(i + 2);
                    int u = Character.digit(hex1, 16);
                    int l = Character.digit(hex2, 16);
                    if (u == -1 || l == -1) {
                        throw new IllegalArgumentException("Invalid encoded sequence \"" + source.substring(i) + "\"");
                    }
                    baos.write((char) ((u << 4) + l));
                    i += 2;
                    changed = true;
                }
                else {
                    throw new IllegalArgumentException("Invalid encoded sequence \"" + source.substring(i) + "\"");
                }
            }
            else {
                baos.write(ch);
            }
        }
        return (changed ? new String(baos.toByteArray(), StandardCharsets.UTF_8) : source);
    }

}
