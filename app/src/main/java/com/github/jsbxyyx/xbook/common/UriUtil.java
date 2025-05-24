package com.github.jsbxyyx.xbook.common;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author jsbxyyx
 */
public class UriUtil {

    public static String urlEncode(String source) {
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

}
