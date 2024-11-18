package com.github.jsbxyyx.xbook.common;

import android.app.Activity;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
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
    public static final byte MG_XOR = 7;
    public static final String comma = ",";
    public static final String vc = "vc";
    public static final String vn = "vn";
    public static final String sv = "sv";
    public static final String platform = "platform";
    public static final String platform_android = "android";

    public static final String login_key = "userdata";
    public static final String profile_nickname_key = "profile_nickname";
    public static final String profile_email_key = "profile_email";
    public static final String search_ext_key = "search_ext";
    public static final String search_language_key = "search_language";
    public static final String sync_key = "sync";
    public static final String reader_image_show_key = "reader_image_show";
    public static final String checked = "1";
    public static final String unchecked = "0";

    public static final String log_suffix = ".exception";
    public static final String book_metadata_suffix = ".meta";

    public static final String action_delete = "delete";
    public static final String action_upload = "upload";
    public static String action_download_meta = "download_meta";
    public static final String action_file_download = "file_download";
    public static final String action_image_hide = "image_hide";

    public static final String x_message = "X-message";
    public static final String serv_userid = "remix_userid";
    public static final String serv_userkey = "remix_userkey";

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

    public static byte[] xor(byte[] rawData, int len, byte number) {
        byte[] encodeData = new byte[len];
        for (int i = 0; i < len; i++) {
            encodeData[i] = (byte) (rawData[i] ^ number);
        }
        return encodeData;
    }

    public static void copyAssets(Activity activity, String asset, String dest) throws IOException {
        String[] assets = activity.getResources().getAssets().list(asset);
        File destDir = new File(dest);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        for (String file : assets) {
            try (InputStream in = activity.getResources().getAssets().open(asset + "/" + file);
                 FileOutputStream out = new FileOutputStream(new File(destDir, file))) {
                copy(in, out);
            }
        }
    }

}
