package com.github.jsbxyyx.xbook.tts;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class TTSClient extends WebSocketClient {

    private static String BASE_URL = "speech.platform.bing.com/consumer/speech/synthesize/readaloud";
    private static String TRUSTED_CLIENT_TOKEN = "6A5AA1D4EAFF4E9FB37E23D68491D6F4";

    private static String WSS_URL = "wss://{BASE_URL}/edge/v1?TrustedClientToken={TRUSTED_CLIENT_TOKEN}"
            .replace("{BASE_URL}", BASE_URL).replace("{TRUSTED_CLIENT_TOKEN}", TRUSTED_CLIENT_TOKEN);
    private static String VOICE_LIST_URL = "https://{BASE_URL}/voices/list?trustedclienttoken={TRUSTED_CLIENT_TOKEN}"
            .replace("{BASE_URL}", BASE_URL).replace("{TRUSTED_CLIENT_TOKEN}", TRUSTED_CLIENT_TOKEN);

    private static String DEFAULT_VOICE = "zh-CN-XiaoxiaoNeural";

    private static String CHROMIUM_FULL_VERSION = "130.0.2849.68";
    private static String CHROMIUM_MAJOR_VERSION = CHROMIUM_FULL_VERSION.split("\\.", 2)[0];
    private static String SEC_MS_GEC_VERSION = "1-{CHROMIUM_FULL_VERSION}"
            .replace("{CHROMIUM_FULL_VERSION}", CHROMIUM_FULL_VERSION);

    private static Map<String, String> BASE_HEADERS = new HashMap() {
        {
            put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/{CHROMIUM_MAJOR_VERSION}.0.0.0 Safari/537.36 Edg/{CHROMIUM_MAJOR_VERSION}.0.0.0"
                    .replace("{CHROMIUM_MAJOR_VERSION}", CHROMIUM_MAJOR_VERSION));
            put("Accept-Encoding", "gzip, deflate, br");
            put("Accept-Language", "en-US,en;q=0.9");
        }
    };
    private static Map<String, String> WSS_HEADERS = new HashMap() {
        {
            put("Pragma", "no-cache");
            put("Cache-Control", "no-cache");
            put("Origin", "chrome-extension://jdiccldimpdaibmpdkjnbmckianbfold");
            putAll(BASE_HEADERS);
        }
    };
    private static Map<String, String> VOICE_HEADERS = new HashMap() {
        {
            put("Authority", "speech.platform.bing.com");
            put("Sec-CH-UA", "\" Not;A Brand\";v=\"99\", \"Microsoft Edge\";v=\"{CHROMIUM_MAJOR_VERSION}\", \"Chromium\";v=\"{CHROMIUM_MAJOR_VERSION}\""
                    .replace("{CHROMIUM_MAJOR_VERSION}", CHROMIUM_MAJOR_VERSION));
            put("Sec-CH-UA-Mobile", "?0");
            put("Accept", "*/*");
            put("Sec-Fetch-Site", "none");
            put("Sec-Fetch-Mode", "cors");
            put("Sec-Fetch-Dest", "empty");
            putAll(BASE_HEADERS);
        }
    };

    public static boolean debug = false;
    private final ByteArrayOutputStream buffers = new ByteArrayOutputStream(1024);
    private final CompletableFuture<ByteArrayOutputStream> cf;
    private static Appendable appendable;

    public TTSClient(URI serverUri, Map<String, String> httpHeaders, CompletableFuture<ByteArrayOutputStream> cf) {
        super(serverUri, httpHeaders);
        this.cf = cf;
    }

    public static void setAppendable(Appendable append) {
        appendable = append;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        String CONFIG_PATTERN = "X-Timestamp:%s\r\n" +
                "Content-Type:application/json; charset=utf-8\r\n" +
                "Path:speech.config\r\n\r\n" +
                "{\"context\":{\"synthesis\":{\"audio\":{\"metadataoptions\":{\"sentenceBoundaryEnabled\":\"false\",\"wordBoundaryEnabled\":\"true\"},\"outputFormat\":\"%s\"}}}}";
        send(String.format(
                CONFIG_PATTERN,
                date(),
                "audio-24khz-48kbitrate-mono-mp3")
        );
    }

    @Override
    public void onMessage(String text) {
        log("onMessage:\r\n" + text);
        if (text.contains("turn.start")) {
            // （新的）音频流开始传输开始，清空重置buffer
            buffers.reset();
        } else if (text.contains("turn.end")) {
            // 音频流结束，写为文件
            try {
                cf.complete(buffers);
                close(1000);
            } catch (Exception e) {
                log(e.getMessage());
            }
        }
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        // Content-Type:audio
        // Path:audio\r\n
        byte[] rawData = bytes.array();
        byte[] sep = "Path:audio\r\n".getBytes(StandardCharsets.UTF_8);
        int index = indexOf(rawData, sep);
        byte[] data = new byte[rawData.length - (index + sep.length)];
        System.arraycopy(rawData, index + sep.length, data, 0, data.length);
        try {
            buffers.write(data);
        } catch (IOException ignore) {
        }
        log("rawData:" + encodeHex(rawData));
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log("onClose code:" + code + ", reason:" + reason + ", remote:" + remote);
        if (code != 1000) {
            cf.complete(new ByteArrayOutputStream());
        }
    }

    @Override
    public void onError(Exception e) {
        cf.complete(new ByteArrayOutputStream());
        log("onClose e:" + e.getMessage());
    }

    static void log(String str) {
        if (debug) {
            System.out.println(str);
        }
        if (appendable != null) {
            try {
                appendable.append(str).append("\n");
            } catch (IOException ignore) {
            }
        }
    }

    static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    static String date() {
        String format = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss", Locale.US).format(new Date());
        return format + " GMT+0000 (Coordinated Universal Time)";
    }

    static String ssml_with_data(String ssml) {
        return String.format("X-RequestId:%s\r\n" +
                "Content-Type:application/ssml+xml\r\n" +
                "X-Timestamp:%sZ\r\n" +
                "Path:ssml\r\n\r\n" +
                "%s", uuid(), date(), ssml);
    }

    static String sec_ms_gec() {
        long ticks = (long) (Math.floor((System.currentTimeMillis() / 1000.0) + 11644473600L) * 10000000);
        long roundedTicks = ticks - (ticks % 3000000000L);
        String str = roundedTicks + TRUSTED_CLIENT_TOKEN;
        return encodeHex(sha256(str.getBytes(StandardCharsets.UTF_8))).toUpperCase();
    }

    static String connect_url() {
        return "{WSS_URL}&Sec-MS-GEC={Sec_MS_GEC}&Sec-MS-GEC-Version={SEC_MS_GEC_VERSION}&ConnectionId={CONNECTION_ID}"
                .replace("{WSS_URL}", WSS_URL)
                .replace("{Sec_MS_GEC}", sec_ms_gec())
                .replace("{SEC_MS_GEC_VERSION}", SEC_MS_GEC_VERSION)
                .replace("{CONNECTION_ID}", uuid());
    }

    static int indexOf(byte[] array, byte[] target) {
        if (array == null) throw new NullPointerException("array");
        if (target == null) throw new NullPointerException("target");
        if (target.length == 0) {
            return 0;
        }
        outer:
        for (int i = 0; i < array.length - target.length + 1; i++) {
            for (int j = 0; j < target.length; j++) {
                if (array[i + j] != target[j]) {
                    continue outer;
                }
            }
            return i;
        }
        return -1;
    }

    static String encodeHex(final byte[] data) {
        char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        final int dataLength = data.length;
        final char[] out = new char[dataLength << 1];
        for (int i = 0, j = 0; i < dataLength; i++) {
            out[j++] = DIGITS_LOWER[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS_LOWER[0x0F & data[i]];
        }
        return new String(out);
    }

    static byte[] sha256(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(data);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static ByteArrayOutputStream audioBySsml(String ssml) throws Exception {
        CompletableFuture<ByteArrayOutputStream> cf = new CompletableFuture<>();
        // Microsoft Server Speech Text to Speech Voice (en-US, AriaNeural)
        // zh-CN-XiaoxiaoNeural

        TTSClient client2 = new TTSClient(new URI(connect_url()), WSS_HEADERS, cf);
        client2.connect();
        while (!client2.isOpen()) {
            Thread.sleep(50);
            if (client2.isClosed()) {
                log("client is closed.");
                return new ByteArrayOutputStream();
            }
        }
        String ssmlWithData = ssml_with_data(ssml);
        log("ssml:[\n" + ssmlWithData + "\n]");
        client2.send(ssmlWithData);
        ByteArrayOutputStream output = cf.get(30, TimeUnit.SECONDS);
        return output;
    }

    public static ByteArrayOutputStream audioByText(String text, String lang, String voiceName, String pitch, String rate, String volume) throws Exception {
        if (lang == null) lang = "zh-CN";
        if (voiceName == null) voiceName = DEFAULT_VOICE;
        if (pitch == null) pitch = "+0Hz";
        if (rate == null) rate = "+0%";
        if (volume == null) volume = "+0%";
        final String SPEAK_PATTERN = "<speak version='1.0' xmlns='http://www.w3.org/2001/10/synthesis' xml:lang='%s'>" +
                "<voice name='%s'>" +
                "<prosody pitch='%s' rate='%s' volume='%s'>" +
                "%s" +
                "</prosody>" +
                "</voice>" +
                "</speak>";
        String ssml = String.format(
                SPEAK_PATTERN,
                lang,
                voiceName,
                pitch,
                rate,
                volume,
                text
        );
        return audioBySsml(ssml);
    }

    static void tts() throws Exception {
        ByteArrayOutputStream output = audioByText("你好", null, null, null, null, null);
        if (output.size() > 0) {
            String name = System.currentTimeMillis() + ".mp3";
            Files.write(new File(System.getProperty("user.dir") + "/" + name).toPath(), output.toByteArray());
            log("write " + name);
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println(VOICE_LIST_URL);
        System.out.println(date());
    }

}