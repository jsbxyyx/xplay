package com.github.jsbxyyx.xbook.common;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;

import com.github.jsbxyyx.xbook.data.bean.Ip;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author jsbxyyx
 */
public class Common {

    public static final String sdcard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();

    private static final List<Ip> IPS = new ArrayList<>();

    public static final String host = new String(Ba.abtoa("a([0c$)u:j!w:$!w:$!x.nh5eg=="), StandardCharsets.UTF_8);
    public static final String tts_host = new String(Ba.abtoa("d([z.j)w:$!w:$!w:]54e|o="), StandardCharsets.UTF_8);
    private static final String xurl = "https://" + host + "/xbook";
    private static final String xburl = "https://" + host + "/xbookb";
    private static final String ttsurl = "https://" + tts_host + "/v1/audio/speech";
    public static final String zurl = "";
    public static final String xbook_dir = sdcard + "/xbook";
    public static final long MAGIC = Long.parseLong("CAFEBABE", 16);
    public static final byte MG_XOR = 7;
    public static final String comma = ",";
    public static final String header_vc = "vc";
    public static final String header_vn = "vn";
    public static final String header_sv = "sv";
    public static final String header_platform = "platform";
    public static final String platform_android = "android";

    public static final String login_key = "userdata";
    public static final String profile_nickname_key = "profile_nickname";
    public static final String profile_email_key = "profile_email";
    public static final String search_ext_key = "search_ext";
    public static final String search_language_key = "search_language";
    public static final String sync_key = "sync";
    public static final String reader_image_show_key = "reader_image_show";
    public static final String online_read_key = "online_read";
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
    public static final String TYPE_BL = "BL";
    public static final String TYPE_B = "B";
    public static final String downloaded = "downloaded";
    public static final String not_downloaded = "not_downloaded";

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

    public static <E> boolean isEmpty(List<E> list) {
        return list == null || list.isEmpty();
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

    public static void copyAssets(Context context, String asset, String dest) throws IOException {
        AssetManager assetManager = context.getAssets();
        String[] assets = assetManager.list(asset);
        File destDir = new File(dest);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        if (assets != null) {
            for (String file : assets) {
                String assetPath = asset + "/" + file;
                File targetFile = new File(destDir, file);
                if (isDirectory(assetManager, assetPath)) {
                    if (!targetFile.exists()) {
                        targetFile.mkdirs();
                    }
                    copyAssets(context, assetPath, targetFile.getAbsolutePath());
                } else {
                    try (InputStream in = assetManager.open(assetPath);
                         FileOutputStream out = new FileOutputStream(targetFile)) {
                        copy(in, out);
                    }
                }
            }
        }
    }

    public static boolean isDirectory(AssetManager assetManager, String assetPath) {
        try {
            String[] files = assetManager.list(assetPath);
            return files != null && files.length > 0;
        } catch (IOException e) {
            return false;
        }
    }

    public static String getXurl() {
        return xurl;
    }

    public static String getXburl() {
        return xburl;
    }

    public static String getTtsurl() {
        return ttsurl;
    }

    public static void setIPS(List<Ip> ips) {
        if (!isEmpty(ips)) {
            IPS.clear();
            IPS.addAll(ips);
        }
    }

    public static List<Ip> getIPS() {
        return IPS;
    }

    public static String getIp() {
        if (IPS.isEmpty()) {
            return host;
        }
        return IPS.get(0).getIp();
    }

    public static <T> Map<String, T> listToMap(List<T> list, Function<T, String> f) {
        if (list == null || list.isEmpty()) {
            return new HashMap<>();
        }
        Map<String, T> map = new LinkedHashMap<>(list.size());
        for (T t : list) {
            String key = f.apply(t);
            map.put(key, t);
        }
        return map;
    }

    public static <T> Map<String, List<T>> listToMap2(List<T> list, Function<T, String> f) {
        if (list == null || list.isEmpty()) {
            return new HashMap<>();
        }
        Map<String, List<T>> map = new LinkedHashMap<>(list.size());
        for (T t : list) {
            String key = f.apply(t);
            map.putIfAbsent(key, new ArrayList<>());
            map.get(key).add(t);
        }
        return map;
    }

    public static Map<String, Object> newMap(Object... objects) {
        Map<String, Object> map = new LinkedHashMap<>();
        if (objects != null && objects.length > 1 && objects.length % 2 == 0) {
            for (int i = 0; i < objects.length; ) {
                String key = objects[i++].toString();
                Object value = objects[i++];
                map.put(key, value);
            }
        }
        return map;
    }

    public static <T> List<T> newList(T... objects) {
        List<T> list = new ArrayList<>();
        if (objects != null && objects.length > 0) {
            for (T object : objects) {
                list.add(object);
            }
        }
        return list;
    }

}
