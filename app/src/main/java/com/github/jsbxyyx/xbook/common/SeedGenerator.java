package com.github.jsbxyyx.xbook.common;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SeedGenerator {

    /**
     * 从HTML中提取40位hex seed
     */
    public static String extractSeed(String html) {
        Pattern pattern = Pattern.compile("[\"']([A-Fa-f0-9]{40})[\"']");
        Matcher matcher = pattern.matcher(html);
        if (!matcher.find()) {
            throw new RuntimeException("未能从页面中解析到 seed");
        }
        return matcher.group(1);
    }

    public static String extractCookie(String html) {
        String sep = ";resp_cookie:";
        int idx = html.indexOf(sep);
        if (idx > -1) {
            return html.substring(idx + sep.length());
        }
        return "";
    }


    public static Map<String,Object> generate(String seed) throws Exception {


        // JS:
        // parseInt('0x'+c[0])
        int n1 = Integer.parseInt(
                String.valueOf(seed.charAt(0)),
                16
        );


        MessageDigest sha1 =
                MessageDigest.getInstance("SHA-1");


        long start = System.nanoTime();


        long i = 0;


        while (true) {


            String input = seed + i;


            byte[] digest =
                    sha1.digest(
                            input.getBytes(StandardCharsets.UTF_8)
                    );


            /*
             JS:
             s[n1] === 0xb0
             s[n1+1] === 0xb

             注意：
             JS数组里的byte是有符号，但是比较数字。
             s[n1+1]实际应该是 0x0b
             */

            if ((digest[n1] & 0xff) == 0xb0
                    &&
                    (digest[n1 + 1] & 0xff) == 0x0b) {


                double ctime =
                        (System.nanoTime()-start)
                                /1000000000.0;


                Map<String,Object> result =
                        new HashMap<>();

                result.put(
                        "i",
                        i
                );

                // Cookie里面的值
                result.put(
                        "c_token",
                        seed + i
                );


                result.put(
                        "c_time",
                        String.valueOf(ctime)
                );


                return result;
            }


            i++;


            if(i % 50000 == 0){
                Thread.yield();
            }
        }
    }


    public static void main(String[] args) throws Exception {

        String html = "\"B57BA9E5093F594544A04AD10759CE219C53517C\"";

        String seed = extractSeed(html);
        System.out.println("seed=" + seed);

        Map<String, Object> result = generate(seed);
        System.out.println(result);
    }
}