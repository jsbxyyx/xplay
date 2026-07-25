package com.github.jsbxyyx.xbook.common;

import java.util.LinkedHashMap;
import java.util.Map;

public final class CookieUtils {

    private CookieUtils() {
    }

    /**
     * Cookie字符串转Map
     */
    public static Map<String, String> parse(String cookie) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();

        if (cookie == null || cookie.trim().isEmpty()) {
            return map;
        }

        String[] items = cookie.split(";");

        for (String item : items) {
            item = item.trim();
            if (item.isEmpty()) {
                continue;
            }

            int index = item.indexOf('=');
            if (index <= 0) {
                continue;
            }

            String key = item.substring(0, index).trim();
            String value = item.substring(index + 1).trim();

            map.put(key, value);
        }

        return map;
    }

    /**
     * Map转Cookie字符串
     */
    public static String toCookie(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (sb.length() > 0) {
                sb.append("; ");
            }

            sb.append(entry.getKey())
              .append("=")
              .append(entry.getValue());
        }

        return sb.toString();
    }

    /**
     * 获取Cookie值
     */
    public static String get(String cookie, String key) {
        return parse(cookie).get(key);
    }

    /**
     * 是否存在
     */
    public static boolean contains(String cookie, String key) {
        return parse(cookie).containsKey(key);
    }

    /**
     * 设置Cookie（存在则修改，不存在则新增）
     */
    public static String put(String cookie, String key, String value) {
        Map<String, String> map = parse(cookie);
        map.put(key, value);
        return toCookie(map);
    }

    /**
     * 删除Cookie
     */
    public static String remove(String cookie, String key) {
        Map<String, String> map = parse(cookie);
        map.remove(key);
        return toCookie(map);
    }

    /**
     * 一次设置多个Cookie
     */
    public static String putAll(String cookie, Map<String, String> values) {
        Map<String, String> map = parse(cookie);
        map.putAll(values);
        return toCookie(map);
    }
}