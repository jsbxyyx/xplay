package com.github.jsbxyyx.xbook.common;

import android.os.Environment;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author jsbxyyx
 * @since 1.0
 */
public class Common {

    public static final String sdcard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();

    public static final String xurl = "https://http2.idingdang.org/xbook";
    public static final String xburl = "https://http2.idingdang.org/xbookb";
    public static final String zurl = "";
    public static final String xbook_dir = sdcard + "/xbook";

    public static final String login_key = "userdata";

    public static String profile_nickname_key = "profile_nickname";
    public static String profile_email_key = "profile_email";
    public static String log_suffix = ".exception";

    public static String book_metadata_suffix = ".meta";

    public static String action_delete = "delete";
    public static String action_upload = "upload";

    public static String urlEncode(String str) {
        try {
            return URLEncoder.encode(str, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static boolean statusSuccessful(int status) {
        return status >= 200 && status <= 299;
    }

    public static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[8192];
        int length;
        while ((length = in.read(buf)) != -1) {
            out.write(buf, 0, length);
            out.flush();
        }
    }

}
