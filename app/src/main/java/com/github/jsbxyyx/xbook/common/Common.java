package com.github.jsbxyyx.xbook.common;

import android.os.Environment;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jsbxyyx
 */
public class Common {

    public static final String sdcard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();

    public static final String xurl = "https://http2.idingdang.org/xbook";
    public static final String xburl = "https://http2.idingdang.org/xbookb";
    public static final String zurl = "";
    public static final String xbook_dir = sdcard + "/xbook";
    public static final long MAGIC = Long.parseLong("CAFEBABE", 16);
    public static String comma = ",";

    public static final String login_key = "userdata";
    public static String profile_nickname_key = "profile_nickname";
    public static String profile_email_key = "profile_email";
    public static String search_ext_key = "search_ext";
    public static String search_language_key = "search_language";
    public static String sync_key = "sync";
    public static String reader_image_show_key = "reader_image_show";
    public static String checked = "1";
    public static String unchecked = "0";

    public static String log_suffix = ".exception";
    public static String book_metadata_suffix = ".meta";

    public static String action_delete = "delete";
    public static String action_upload = "upload";
    public static String action_download_meta = "download_meta";
    public static String action_file_download = "file_download";
    public static String action_image_hide = "image_hide";

    public static String x_message = "X-message";
    public static String serv_userid = "remix_userid";
    public static String serv_userkey = "remix_userkey";

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

    public static List<String> split(String str, String regex) {
        if (str == null || str.length() == 0) {
            return new ArrayList<>();
        }
        String[] split = str.split(regex);
        if (split.length == 0) {
            return new ArrayList<>();
        }
        List<String> list = new ArrayList<>(split.length);
        for (String s : split) {
            if (s != null && !"".equals(s.trim())) {
                list.add(s.trim());
            }
        }
        return list;
    }

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isNull(String str) {
        return str == null;
    }

    public static String toString(Object obj) {
        return obj == null ? "" : obj.toString();
    }

    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignore) {
        }
    }

    public static String trim(String str) {
        return str == null ? "" : str.trim();
    }

    public static Map<String, String> parseKv(String str) {
        Map<String, String> kvMap = new HashMap<>();
        if (isBlank(str)) {
            return kvMap;
        }
        String[] cookies = str.split("\\;");
        for (String cookie : cookies) {
            String[] keyValue = cookie.split("\\=", 2);
            if (keyValue.length == 2) {
                kvMap.put(trim(keyValue[0]), trim(keyValue[1]));
            }
        }
        return kvMap;
    }

}
