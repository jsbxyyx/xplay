package com.github.jsbxyyx.xbook.data;

import static com.github.jsbxyyx.xbook.common.Common.getXurl;
import static com.github.jsbxyyx.xbook.common.Common.getXburl;
import static com.github.jsbxyyx.xbook.common.Common.zurl;
import static com.github.jsbxyyx.xbook.common.UriUtil.urlEncode;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.jsbxyyx.xbook.common.Common;
import com.github.jsbxyyx.xbook.common.ContentDispositionParser;
import com.github.jsbxyyx.xbook.common.DataCallback;
import com.github.jsbxyyx.xbook.common.HttpStatusException;
import com.github.jsbxyyx.xbook.common.JsonUtil;
import com.github.jsbxyyx.xbook.common.LogUtil;
import com.github.jsbxyyx.xbook.common.ProgressListener;
import com.github.jsbxyyx.xbook.common.SessionManager;
import com.github.jsbxyyx.xbook.common.UiUtils;
import com.github.jsbxyyx.xbook.data.bean.Book;
import com.github.jsbxyyx.xbook.data.bean.MLog;
import com.github.jsbxyyx.xbook.data.bean.Profile;
import com.github.jsbxyyx.xbook.httpserver.MediaTypeFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author jsbxyyx
 * @since 1.0
 */
public class BookNetHelper {

    private String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36";
    private String content_type_key = "content-type";
    private String cookie_key = "cookie";

    private String TAG = getClass().getSimpleName();

    public void search(String keyword, int page, List<String> languages, List<String> extensions, DataCallback<List<Book>> dataCallback) {
        Map<String, Object> object = new HashMap<>();

        String reqUrl = zurl + "/s/" + urlEncode(keyword);
        object.put("method", "GET");
        object.put("url", reqUrl);

        Map<String, Object> headers = new HashMap<>();
        headers.put("User-Agent", userAgent);
        headers.put(cookie_key, SessionManager.getSession());
        object.put("headers", headers);

        List<Object> params = new ArrayList<>();
        List<String> p0 = new ArrayList<>();
        p0.add("page");
        p0.add(page + "");
        params.add(p0);
        if (languages != null && !languages.isEmpty()) {
            for (String name : languages) {
                List<String> lang = new ArrayList<>();
                lang.add("languages[]");
                lang.add(name);
                params.add(lang);
            }
        }
        if (extensions != null && !extensions.isEmpty()) {
            for (String name : extensions) {
                List<String> ext = new ArrayList<>();
                ext.add("extensions[]");
                ext.add(name);
                params.add(ext);
            }
        }
        object.put("params", params);

        String s = JsonUtil.toJson(object);
        LogUtil.d(TAG, "search request: %s : %s", reqUrl, s);
        Request.Builder builder = new Request.Builder()
                .url(getXurl())
                .post(RequestBody.create(s, MediaType.parse("application/json")));
        setCommonHeader(builder);
        HttpHelper.getClient().newCall(builder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                LogUtil.d(TAG, "onFailure: %s", LogUtil.getStackTraceString(e));
                dataCallback.call(new ArrayList<>(), e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    dataCallback.call(new ArrayList<>(), new HttpStatusException(response.header(Common.x_message) + "", response.code(), reqUrl));
                    return;
                }
                String string = response.body().string();
                LogUtil.d(TAG, "search response: %s", string);
                try {
                    JsonNode jsonObject = JsonUtil.readTree(string);
                    int status = jsonObject.get("status").asInt();
                    if (!Common.statusSuccessful(status)) {
                        dataCallback.call(new ArrayList<>(), new HttpStatusException(response.header(Common.x_message) + "", status, reqUrl));
                        return;
                    }
                    JsonNode data = jsonObject.get("data");
                    LogUtil.d(TAG, "total: %s", data.get("total").asText());
                    List<Book> list = JsonUtil.convertValue(data.get("list"), new TypeReference<List<Book>>() {
                    });
                    dataCallback.call(list, null);
                } catch (Exception e) {
                    dataCallback.call(new ArrayList<>(), e);
                }
            }
        });
    }

    public void detail(String detailUrl, DataCallback dataCallback) {
        Map<String, Object> object = new HashMap<>();
        String reqUrl = detailUrl;
        object.put("method", "GET");
        object.put("url", reqUrl);

        Map<String, Object> headers = new HashMap<>();
        headers.put("User-Agent", userAgent);
        headers.put(cookie_key, SessionManager.getSession());
        object.put("headers", headers);

        Map<String, Object> params = new HashMap<>();
        object.put("params", params);

        String s = JsonUtil.toJson(object);
        LogUtil.d(TAG, "detail request: %s : %s", reqUrl, s);

        Request.Builder builder = new Request.Builder()
                .url(getXurl())
                .post(RequestBody.create(s, MediaType.parse("application/json")));
        setCommonHeader(builder);
        HttpHelper.getClient().newCall(builder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                LogUtil.d(TAG, "onFailure: %s", LogUtil.getStackTraceString(e));
                dataCallback.call(null, e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    LogUtil.d(TAG, "onResponse: %s", response.code());
                    dataCallback.call(null, new HttpStatusException(response.header(Common.x_message) + "", response.code(), reqUrl));
                    return;
                }
                String string = response.body().string();
                LogUtil.d(TAG, "detail response: %s", string);
                try {
                    JsonNode jsonObject = JsonUtil.readTree(string);
                    int status = jsonObject.get("status").asInt();
                    if (!Common.statusSuccessful(status)) {
                        dataCallback.call(null, new HttpStatusException(response.header(Common.x_message) + "", status, reqUrl));
                        return;
                    }
                    JsonNode data = jsonObject.get("data");
                    Book book = JsonUtil.convertValue(data, new TypeReference<Book>() {
                    });
                    dataCallback.call(book, null);
                } catch (Exception e) {
                    dataCallback.call(null, e);
                }
            }
        });
    }

    public void login(String email, String password, DataCallback dataCallback) {
        Map<String, Object> object = new HashMap<>();
        String reqUrl = zurl + "/rpc.php";
        object.put("method", "POST");
        object.put("url", reqUrl);

        Map<String, Object> headers = new HashMap<>();
        headers.put("User-Agent", userAgent);
        headers.put(content_type_key, "application/x-www-form-urlencoded; charset=UTF-8");
        object.put("headers", headers);

        Map<String, Object> params = new HashMap<>();
        object.put("params", params);

        StringBuilder data = new StringBuilder();
        data.append("isModal=true").append("&");
        data.append("email=").append(urlEncode(email)).append("&");
        data.append("password=").append(urlEncode(password)).append("&");
        data.append("site_mode=books").append("&");
        data.append("action=login").append("&");
        data.append("redirectUrl=").append(urlEncode(zurl)).append("&");
        data.append("gg_json_mode=1");
        object.put("data", data.toString());

        String s = JsonUtil.toJson(object);
        LogUtil.d(TAG, "login request: %s : %s", reqUrl, s);

        Request.Builder builder = new Request.Builder()
                .url(getXurl())
                .post(RequestBody.create(s, MediaType.parse("application/json")));
        setCommonHeader(builder);
        HttpHelper.getClient().newCall(builder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                LogUtil.d(TAG, "onFailure: ", LogUtil.getStackTraceString(e));
                dataCallback.call(null, e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    LogUtil.d(TAG, "onResponse: %s", response.code());
                    dataCallback.call(null, new HttpStatusException(response.header(Common.x_message) + "", response.code(), reqUrl));
                    return;
                }
                String string = response.body().string();
                LogUtil.d(TAG, "login response: %s", string);
                JsonNode jsonObject = JsonUtil.readTree(string);
                int status = jsonObject.get("status").asInt();
                if (!Common.statusSuccessful(status)) {
                    LogUtil.d(TAG, "onResponse: %s", status);
                    dataCallback.call(null, new HttpStatusException(response.header(Common.x_message) + "", status, reqUrl));
                    return;
                }
                JsonNode respData = JsonUtil.readTree(jsonObject.get("data").asText());
                JsonNode respResponse = respData.get("response");
                if (!respResponse.has("forceRedirection")) {
                    dataCallback.call(null, new HttpStatusException(respResponse.get("message").asText(), status, reqUrl));
                    return;
                }
                JsonNode respHeaders = jsonObject.get("headers");
                String forceRedirection = respResponse.get("forceRedirection").asText();
                String session = forceRedirection.substring(2).replace("&", ";") + ";";
                session += respHeaders.get("set-cookie") != null ? respHeaders.get("set-cookie").asText() : "";
                dataCallback.call(session, null);
            }
        });
    }

    public void download(String downloadUrl, String destDir, String uid,
                         DataCallback dataCallback, ProgressListener listener) {
        downloadWithMagic(downloadUrl, destDir, uid, dataCallback, listener, 0);
    }

    public void downloadWithMagic(String downloadUrl, String destDir, String uid,
                                  DataCallback dataCallback, ProgressListener listener, long magic) {
        downloadWithCookie(downloadUrl, destDir, uid, SessionManager.getSession(), dataCallback, listener, magic);
    }

    public void downloadWithCookie(String downloadUrl, String destDir, String uid, String cookie,
                                   DataCallback dataCallback, ProgressListener listener, long magic) {
        Map<String, Object> object = new HashMap<>();
        String reqUrl = downloadUrl;
        object.put("method", "GET");
        object.put("url", reqUrl);

        Map<String, Object> headers = new HashMap<>();
        headers.put("User-Agent", userAgent);
        headers.put(cookie_key, cookie);
        headers.put("b", "1");
        object.put("headers", headers);

        Map<String, Object> params = new HashMap<>();
        object.put("params", params);

        String s = JsonUtil.toJson(object);
        LogUtil.d(TAG, "download request: %s : %s", reqUrl, s);
        Request.Builder builder = new Request.Builder()
                .url(getXurl())
                .post(RequestBody.create(s, MediaType.parse("application/json")));
        setCommonHeader(builder);
        HttpHelper.getSyncClient().newCall(builder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                LogUtil.d(TAG, "onFailure: %s", LogUtil.getStackTraceString(e));
                dataCallback.call(null, e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    LogUtil.d(TAG, "onResponse: %s", response.code());
                    dataCallback.call(null, new HttpStatusException(response.header(Common.x_message) + "", response.code(), reqUrl));
                    return;
                }
                File dir = new File(destDir);
                if (!dir.exists()) {
                    boolean mkdirs = dir.mkdirs();
                    LogUtil.d(TAG, "mkdirs: %s", mkdirs);
                }
                String contentDisposition = response.headers().get("Content-Disposition");
                LogUtil.d(TAG, "contentDisposition: %s", contentDisposition);

                String filename = "";
                if (Common.isBlank(contentDisposition)) {
                    int idx = reqUrl.lastIndexOf("/");
                    if (idx > -1) {
                        filename = reqUrl.substring(idx + 1);
                    }
                } else {
                    filename = ContentDispositionParser.parse(contentDisposition);
                }
                filename = Common.isBlank(filename) ? "tmp-" + UUID.randomUUID().toString() : filename;

                File f = new File(destDir, Common.isEmpty(uid) ? filename : uid + "-" + filename);
                long total = response.body().contentLength();
                try (InputStream input = response.body().byteStream();
                     FileOutputStream output = new FileOutputStream(f)) {
                    if (magic > 0) {
                        long m = magic ^ Common.MG_XOR;
                        ByteBuffer buf = ByteBuffer.allocate(8);
                        buf.putLong(m);
                        buf.flip();
                        byte[] bytes = buf.array();
                        output.write(bytes);
                    }
                    byte[] buffer = new byte[1024 * 8];
                    long count = 0;
                    int n;
                    while (-1 != (n = input.read(buffer))) {
                        if (magic > 0) {
                            output.write(Common.xor(buffer, n, Common.MG_XOR), 0, n);
                        } else {
                            output.write(buffer, 0, n);
                        }
                        count += n;
                        output.flush();
                        if (listener != null) {
                            listener.onProgress(count, total);
                        }
                    }
                    output.flush();
                    if (listener != null) {
                        listener.onProgress(count, total);
                    }
                } catch (IOException e) {
                    UiUtils.showToast("存储失败, 请打开权限");
                }
                dataCallback.call(f, null);
            }
        });
    }

    public void downloadApk(String downloadUrl, DataCallback dataCallback, ProgressListener listener) {
        Map<String, Object> object = new HashMap<>();
        String reqUrl = downloadUrl;
        object.put("method", "GET");
        object.put("url", reqUrl);

        Map<String, Object> headers = new HashMap<>();
        headers.put("User-Agent", userAgent);
        headers.put(cookie_key, SessionManager.getSession());
        headers.put("b", "1");
        object.put("headers", headers);

        Map<String, Object> params = new HashMap<>();
        object.put("params", params);

        String s = JsonUtil.toJson(object);
        LogUtil.d(TAG, "download request: %s : %s", reqUrl, s);
        Request.Builder builder = new Request.Builder()
                .url(getXurl())
                .post(RequestBody.create(s, MediaType.parse("application/json")));
        setCommonHeader(builder);
        HttpHelper.getSyncClient().newCall(builder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                LogUtil.d(TAG, "onFailure: %s", LogUtil.getStackTraceString(e));
                dataCallback.call(null, e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    LogUtil.d(TAG, "onResponse: %s", response.code());
                    dataCallback.call(null, new HttpStatusException(response.header(Common.x_message) + "", response.code(), reqUrl));
                    return;
                }
                File dir = new File(Common.sdcard);
                if (!dir.exists()) {
                    boolean mkdirs = dir.mkdirs();
                    LogUtil.d(TAG, "mkdirs: %s", mkdirs);
                }
                String contentDisposition = response.headers().get("Content-Disposition");
                LogUtil.d(TAG, "contentDisposition: %s", contentDisposition);

                String filename = "";
                if (Common.isBlank(contentDisposition)) {
                    int idx = reqUrl.lastIndexOf("/");
                    if (idx > -1) {
                        filename = reqUrl.substring(idx + 1);
                    }
                } else {
                    filename = ContentDispositionParser.parse(contentDisposition);
                }
                filename = Common.isBlank(filename) ? "tmp-" + UUID.randomUUID().toString() : filename;

                File f = new File(Common.sdcard, filename);
                long total = response.body().contentLength();
                try (InputStream input = response.body().byteStream();
                     FileOutputStream output = new FileOutputStream(f)) {
                    byte[] buffer = new byte[1024 * 8];
                    long count = 0;
                    int n;
                    while (-1 != (n = input.read(buffer))) {
                        output.write(buffer, 0, n);
                        count += n;
                        output.flush();
                        if (listener != null) {
                            listener.onProgress(count, total);
                        }
                    }
                    output.flush();
                    if (listener != null) {
                        listener.onProgress(count, total);
                    }
                }
                dataCallback.call(f, null);
            }
        });
    }

    public void profile(DataCallback dataCallback) {
        Map<String, Object> object = new HashMap<>();
        String reqUrl = zurl + "/profileEdit";
        object.put("method", "GET");
        object.put("url", reqUrl);

        Map<String, Object> headers = new HashMap<>();
        headers.put("User-Agent", userAgent);
        headers.put(cookie_key, SessionManager.getSession());
        object.put("headers", headers);

        Map<String, Object> params = new HashMap<>();
        object.put("params", params);

        String s = JsonUtil.toJson(object);
        LogUtil.d(TAG, "profile request: %s : %s", reqUrl, s);
        Request.Builder builder = new Request.Builder()
                .url(getXurl())
                .post(RequestBody.create(s, MediaType.parse("application/json")));
        setCommonHeader(builder);
        HttpHelper.getClient().newCall(builder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                LogUtil.d(TAG, "onFailure: %s", LogUtil.getStackTraceString(e));
                dataCallback.call(null, e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    LogUtil.d(TAG, "onResponse: %s", response.code());
                    dataCallback.call(null, new HttpStatusException(response.header(Common.x_message) + "", response.code(), reqUrl));
                    return;
                }
                String string = response.body().string();
                LogUtil.d(TAG, "profile response: %s", string);
                JsonNode jsonObject = JsonUtil.readTree(string);
                int status = jsonObject.get("status").asInt();
                if (!Common.statusSuccessful(status)) {
                    LogUtil.d(TAG, "onResponse: %s", status);
                    dataCallback.call(null, new HttpStatusException(response.header(Common.x_message) + "", status, reqUrl));
                    return;
                }
                JsonNode data = jsonObject.get("data");
                Profile profile = JsonUtil.convertValue(data, new TypeReference<Profile>() {
                });
                if (Common.isEmpty(profile.getEmail())) {
                    dataCallback.call(null, new HttpStatusException("未登录", 401, reqUrl));
                    return;
                }
                dataCallback.call(profile, null);
            }
        });
    }

    public void sendCode(String email, String password, String nickname, DataCallback dataCallback) {
        Map<String, Object> object = new HashMap<>();
        String reqUrl = zurl + "/papi/user/verification/send-code";
        object.put("method", "POST");
        object.put("url", reqUrl);

        Map<String, Object> headers = new HashMap<>();
        headers.put("User-Agent", userAgent);
        headers.put(content_type_key, "multipart/form-data");
        object.put("headers", headers);

        Map<String, Object> data = new HashMap<>();
        data.put("email", email);
        data.put("password", password);
        data.put("name", Common.isBlank(nickname) ? urlEncode(email.split("\\@")[0]) : urlEncode(nickname));
        data.put("rx", "215");
        data.put("action", "registration");
        data.put("redirectUrl", "");
        object.put("data", data);

        Map<String, Object> params = new HashMap<>();
        object.put("params", params);

        String s = JsonUtil.toJson(object);
        LogUtil.d(TAG, "send-code request: %s : %s", reqUrl, s);
        Request.Builder builder = new Request.Builder()
                .url(getXurl())
                .post(RequestBody.create(s, MediaType.parse("application/json")));
        setCommonHeader(builder);
        HttpHelper.getClient().newCall(builder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                LogUtil.d(TAG, "onFailure: %s", LogUtil.getStackTraceString(e));
                dataCallback.call(null, e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    LogUtil.d(TAG, "onResponse: %s", response.code());
                    dataCallback.call(null, new HttpStatusException(response.header(Common.x_message) + "", response.code(), reqUrl));
                    return;
                }
                String string = response.body().string();
                LogUtil.d(TAG, "send-code response: %s", string);
                JsonNode jsonObject = JsonUtil.readTree(string);
                int status = jsonObject.get("status").asInt();
                if (!Common.statusSuccessful(status)) {
                    LogUtil.d(TAG, "onResponse: %s", status);
                    dataCallback.call(null, new HttpStatusException(response.header(Common.x_message) + "", status, reqUrl));
                    return;
                }
                String data = jsonObject.get("data").asText();
                JsonNode dataObject = JsonUtil.readTree(data);
                // "data": "{\"success\":1}}" 1success 0error
                int success = dataObject.get("success").asInt();
                dataCallback.call(dataObject, null);
            }
        });
    }

    public void sendCodePasswordRecovery(String email, DataCallback dataCallback) {
        Map<String, Object> object = new HashMap<>();
        String reqUrl = zurl + "/papi/user/verification/send-code";
        object.put("method", "POST");
        object.put("url", reqUrl);

        Map<String, Object> headers = new HashMap<>();
        headers.put("User-Agent", userAgent);
        headers.put(content_type_key, "multipart/form-data");
        object.put("headers", headers);

        Map<String, Object> data = new HashMap<>();
        data.put("email", email);
        data.put("action", "passwordrecovery");
        object.put("data", data);

        Map<String, Object> params = new HashMap<>();
        object.put("params", params);

        String s = JsonUtil.toJson(object);
        LogUtil.d(TAG, "send-code password recovery request: %s : %s", reqUrl, s);
        Request.Builder builder = new Request.Builder()
                .url(getXurl())
                .post(RequestBody.create(s, MediaType.parse("application/json")));
        setCommonHeader(builder);
        HttpHelper.getClient().newCall(builder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                LogUtil.d(TAG, "onFailure: %s", LogUtil.getStackTraceString(e));
                dataCallback.call(null, e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    LogUtil.d(TAG, "onResponse: %s", response.code());
                    dataCallback.call(null, new HttpStatusException(response.header(Common.x_message) + "", response.code(), reqUrl));
                    return;
                }
                String string = response.body().string();
                LogUtil.d(TAG, "send-code password recovery response: %s", string);
                JsonNode jsonObject = JsonUtil.readTree(string);
                int status = jsonObject.get("status").asInt();
                if (!Common.statusSuccessful(status)) {
                    LogUtil.d(TAG, "onResponse: %s", status);
                    dataCallback.call(null, new HttpStatusException(response.header(Common.x_message) + "", status, reqUrl));
                    return;
                }
                String data = jsonObject.get("data").asText();
                JsonNode dataObject = JsonUtil.readTree(data);
                // "data": "{\"success\":1}}" 1success 0error
                int success = dataObject.get("success").asInt();
                dataCallback.call(dataObject, null);
            }
        });
    }

    public void registration(String email, String password, String verifyCode, String nickname, DataCallback dataCallback) {
        Map<String, Object> object = new HashMap<>();
        String reqUrl = zurl + "/rpc.php";
        object.put("method", "POST");
        object.put("url", reqUrl);

        Map<String, Object> headers = new HashMap<>();
        headers.put("User-Agent", userAgent);
        headers.put(content_type_key, "application/x-www-form-urlencoded; charset=UTF-8");
        object.put("headers", headers);

        Map<String, Object> params = new HashMap<>();
        object.put("params", params);

        StringBuilder data = new StringBuilder();
        data.append("isModal=true").append("&");
        data.append("email=").append(urlEncode(email)).append("&");
        data.append("password=").append(urlEncode(password)).append("&");
        data.append("name=").append(Common.isBlank(nickname) ? urlEncode(email.split("\\@")[0]) : urlEncode(nickname)).append("&");
        data.append("rx=215").append("&");
        data.append("action=registration").append("&");
        data.append("redirectUrl=").append("&");
        data.append("verifyCode=").append(verifyCode).append("&");
        data.append("gg_json_mode=1");
        object.put("data", data.toString());

        String s = JsonUtil.toJson(object);
        LogUtil.d(TAG, "registration request: %s : %s", reqUrl, s);

        Request.Builder builder = new Request.Builder()
                .url(getXurl())
                .post(RequestBody.create(s, MediaType.parse("application/json")));
        setCommonHeader(builder);
        HttpHelper.getClient().newCall(builder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                LogUtil.d(TAG, "onFailure: %s", LogUtil.getStackTraceString(e));
                dataCallback.call(null, e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    LogUtil.d(TAG, "onResponse: %s", response.code());
                    dataCallback.call(null, new HttpStatusException(response.header(Common.x_message) + "", response.code(), reqUrl));
                    return;
                }
                String string = response.body().string();
                LogUtil.d(TAG, "registration response: %s", string);
                JsonNode jsonObject = JsonUtil.readTree(string);
                int status = jsonObject.get("status").asInt();
                if (!Common.statusSuccessful(status)) {
                    LogUtil.d(TAG, "onResponse: %s", status);
                    dataCallback.call(null, new HttpStatusException(response.header(Common.x_message) + "", status, reqUrl));
                    return;
                }
                String data = jsonObject.get("data").asText();
                JsonNode dataObject = JsonUtil.readTree(data);
                JsonNode responseObject = dataObject.get("response");
                if (responseObject.get("forceRedirection") != null) {
                    String forceRedirection = responseObject.get("forceRedirection").asText();
                    String session = forceRedirection.substring(2).replace("&", ";") + ";";
                    dataCallback.call(session, null);
                } else {
                    dataCallback.call(null, new IllegalArgumentException(responseObject.get("message").asText()));
                }
            }
        });
    }

    public void cloudSync(Book book, DataCallback dataCallback) {
        cloudSyncMeta(book, dataCallback);
        cloudSyncRaw(book, dataCallback);
    }

    public void cloudSyncMeta(Book book, DataCallback dataCallback) {

        cloudGetMeta(book, new DataCallback<JsonNode>() {
            @Override
            public void call(JsonNode o, Throwable err) {
                if (o != null && o.get("data") != null && o.get("data").get("sha") != null) {
                    String sha = o.get("data").get("sha").asText();
                    book.fillSha(sha);
                }

                Map<String, Object> object = new HashMap<>();
                String reqUrl = "/sync";
                object.put("method", "POST");
                object.put("url", reqUrl);

                Map<String, Object> headers = new HashMap<>();
                headers.put("User-Agent", userAgent);
                headers.put(cookie_key, SessionManager.getSession());
                object.put("headers", headers);

                Map<String, Object> data = new HashMap<>();
                data.put("title", urlEncode(book.getId() + "-" + book.getTitle() + Common.book_metadata_suffix));
                data.put("raw", Base64.getEncoder().encodeToString(JsonUtil.toJson(book).getBytes(StandardCharsets.UTF_8)));
                data.put("sha", book.extractSha());
                object.put("data", data);

                String s = JsonUtil.toJson(object);
                LogUtil.d(TAG, "cloud sync meta request: %s : %s", reqUrl, s);

                Request.Builder builder = new Request.Builder()
                        .url(getXburl())
                        .post(RequestBody.create(s, MediaType.parse("application/json")));
                setCommonHeader(builder);
                HttpHelper.getSyncClient().newCall(builder.build()).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        LogUtil.d(TAG, "onFailure: %s", LogUtil.getStackTraceString(e));
                        dataCallback.call(null, e);
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            LogUtil.d(TAG, "onResponse: %s", response.code());
                            dataCallback.call(null, new HttpStatusException(response.header(Common.x_message) + "", response.code(), reqUrl));
                            return;
                        }
                        String string = response.body().string();
                        LogUtil.d(TAG, "cloud sync meta response: %s", string);
                        JsonNode jsonObject = JsonUtil.readTree(string);
                        int status = jsonObject.get("status").asInt();
                        if (!Common.statusSuccessful(status)) {
                            LogUtil.d(TAG, "onResponse: %s", status);
                            dataCallback.call(null, new HttpStatusException(response.header(Common.x_message) + "", status, reqUrl));
                            return;
                        }
                        dataCallback.call(jsonObject, null);
                    }
                });
            }
        });
    }

    public void cloudSyncRaw(Book book, DataCallback dataCallback) {
        try {
            Map<String, Object> object = new HashMap<>();
            String reqUrl = "/sync";
            object.put("method", "POST");
            object.put("url", reqUrl);

            Map<String, Object> headers = new HashMap<>();
            headers.put("User-Agent", userAgent);
            headers.put(cookie_key, SessionManager.getSession());
            object.put("headers", headers);

            String file_path = book.extractFilePath();
            byte[] bytes = Files.readAllBytes(new File(file_path).toPath());

            Map<String, Object> data = new HashMap<>();
            data.put("title", urlEncode(book.getId() + "-" + book.getTitle() + "." + MediaTypeFactory.getFilenameExtension(file_path)));
            data.put("raw", Base64.getEncoder().encodeToString(bytes));
            object.put("data", data);

            String s = JsonUtil.toJson(object);
            LogUtil.d(TAG, "cloud sync data request: %s : %s", reqUrl, s);
            Request.Builder builder = new Request.Builder()
                    .url(getXburl())
                    .post(RequestBody.create(s, MediaType.parse("application/json")));
            setCommonHeader(builder);
            HttpHelper.getSyncClient().newCall(builder.build()).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    LogUtil.d(TAG, "onFailure: %s", LogUtil.getStackTraceString(e));
                    dataCallback.call(null, e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        LogUtil.d(TAG, "onResponse: %s", response.code());
                        dataCallback.call(null, new HttpStatusException(response.header(Common.x_message) + "", response.code(), reqUrl));
                        return;
                    }
                    String string = response.body().string();
                    LogUtil.d(TAG, "cloud sync data response: %s", string);
                    JsonNode jsonObject = JsonUtil.readTree(string);
                    int status = jsonObject.get("status").asInt();
                    if (!Common.statusSuccessful(status)) {
                        LogUtil.d(TAG, "onResponse: %s", status);
                        dataCallback.call(null, new HttpStatusException(response.header(Common.x_message) + "", status, reqUrl));
                        return;
                    }
                    dataCallback.call(jsonObject, null);
                }
            });
        } catch (Throwable e) {
            dataCallback.call(null, e);
        }
    }

    public void cloudList(DataCallback dataCallback) {
        Map<String, Object> object = new HashMap<>();
        String reqUrl = "/list";
        object.put("method", "POST");
        object.put("url", reqUrl);

        Map<String, Object> headers = new HashMap<>();
        headers.put("User-Agent", userAgent);
        headers.put(cookie_key, SessionManager.getSession());
        object.put("headers", headers);

        Map<String, Object> data = new HashMap<>();
        data.put("title", "");
        data.put("raw", "");
        object.put("data", data);

        String s = JsonUtil.toJson(object);
        LogUtil.d(TAG, "cloud list request: %s : %s", reqUrl, s);

        Request.Builder builder = new Request.Builder()
                .url(getXburl())
                .post(RequestBody.create(s, MediaType.parse("application/json")));
        setCommonHeader(builder);
        HttpHelper.getSyncClient().newCall(builder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                LogUtil.d(TAG, "onFailure: %s", LogUtil.getStackTraceString(e));
                dataCallback.call(null, e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    LogUtil.d(TAG, "onResponse: %s", response.code());
                    dataCallback.call(null, new HttpStatusException(response.header(Common.x_message) + "", response.code(), reqUrl));
                    return;
                }
                String string = response.body().string();
                LogUtil.d(TAG, "cloud list response: %s", string);
                JsonNode jsonObject = JsonUtil.readTree(string);
                int status = jsonObject.get("status").asInt();
                if (!Common.statusSuccessful(status)) {
                    LogUtil.d(TAG, "onResponse: %s", status);
                    dataCallback.call(null, new HttpStatusException(response.header(Common.x_message) + "", status, reqUrl));
                    return;
                }
                dataCallback.call(jsonObject, null);
            }
        });
    }

    public void cloudDownload(String title, String token, DataCallback dataCallback) {
        Map<String, Object> object = new HashMap<>();
        String reqUrl = "/download";
        object.put("method", "POST");
        object.put("url", reqUrl);

        Map<String, Object> headers = new HashMap<>();
        headers.put("User-Agent", userAgent);
        headers.put(cookie_key, SessionManager.getSession());
        object.put("headers", headers);

        Map<String, Object> data = new HashMap<>();
        data.put("title", urlEncode(title));
        data.put("token", token);
        object.put("data", data);

        String s = JsonUtil.toJson(object);
        LogUtil.d(TAG, "cloud download request: %s : %s", reqUrl, s);

        Request.Builder builder = new Request.Builder()
                .url(getXburl())
                .post(RequestBody.create(s, MediaType.parse("application/json")));
        setCommonHeader(builder);
        HttpHelper.getSyncClient().newCall(builder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                LogUtil.d(TAG, "onFailure: %s", LogUtil.getStackTraceString(e));
                dataCallback.call(new byte[0], e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    LogUtil.d(TAG, "onResponse: %s", response.code());
                    dataCallback.call(new byte[0], new HttpStatusException(response.header(Common.x_message) + "", response.code(), reqUrl));
                    return;
                }
                byte[] bytes = response.body().bytes();
                LogUtil.d(TAG, "cloud download response: %s", bytes.length);
                dataCallback.call(bytes, null);
            }
        });
    }

    public void cloudLog(MLog mLog, DataCallback dataCallback) {
        Map<String, Object> object = new HashMap<>();
        String reqUrl = "/log";
        object.put("method", "POST");
        object.put("url", reqUrl);

        Map<String, Object> headers = new HashMap<>();
        headers.put("User-Agent", userAgent);
        headers.put(cookie_key, SessionManager.getSession());
        object.put("headers", headers);

        Map<String, Object> data = new HashMap<>();
        data.put("title", urlEncode(mLog.getTitle()));
        data.put("raw", Base64.getEncoder().encodeToString(mLog.getRaw().getBytes(StandardCharsets.UTF_8)));
        object.put("data", data);

        String s = JsonUtil.toJson(object);
        LogUtil.d(TAG, "log request: %s : %s", reqUrl, s);

        Request.Builder builder = new Request.Builder()
                .url(getXburl())
                .post(RequestBody.create(s, MediaType.parse("application/json")));
        setCommonHeader(builder);

        HttpHelper.getSyncClient().newCall(builder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                LogUtil.d(TAG, "onFailure: %s", LogUtil.getStackTraceString(e));
                dataCallback.call(null, e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    LogUtil.d(TAG, "onResponse: %s", response.code());
                    dataCallback.call(null, new HttpStatusException(response.header(Common.x_message) + "", response.code(), reqUrl));
                    return;
                }
                String string = response.body().string();
                LogUtil.d(TAG, "log response: %s", string);
                JsonNode jsonObject = JsonUtil.readTree(string);
                int status = jsonObject.get("status").asInt();
                if (!Common.statusSuccessful(status)) {
                    LogUtil.d(TAG, "onResponse: %s", status);
                    dataCallback.call(null, new HttpStatusException(response.header(Common.x_message) + "", status, reqUrl));
                    return;
                }
                dataCallback.call(jsonObject, null);
            }
        });
    }

    public void cloudGetMeta(Book book, DataCallback dataCallback) {
        Map<String, Object> object = new HashMap<>();
        String reqUrl = "/get";
        object.put("method", "POST");
        object.put("url", reqUrl);

        Map<String, Object> headers = new HashMap<>();
        headers.put("User-Agent", userAgent);
        headers.put(cookie_key, SessionManager.getSession());
        object.put("headers", headers);

        Map<String, Object> data = new HashMap<>();
        data.put("title", urlEncode(book.getId() + "-" + book.getTitle() + Common.book_metadata_suffix));
        data.put("raw", "");
        object.put("data", data);

        String s = JsonUtil.toJson(object);
        LogUtil.d(TAG, "cloud get meta request: %s : %s", reqUrl, s);

        Request.Builder builder = new Request.Builder()
                .url(getXburl())
                .post(RequestBody.create(s, MediaType.parse("application/json")));
        setCommonHeader(builder);
        HttpHelper.getSyncClient().newCall(builder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                LogUtil.d(TAG, "onFailure: %s", e);
                dataCallback.call(null, e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    LogUtil.d(TAG, "onResponse: %s", response.code());
                    dataCallback.call(null, new HttpStatusException(response.header(Common.x_message) + "", response.code(), reqUrl));
                    return;
                }
                String string = response.body().string();
                LogUtil.d(TAG, "cloud get meta response: %s", string);
                JsonNode jsonObject = JsonUtil.readTree(string);
                int status = jsonObject.get("status").asInt();
                if (!Common.statusSuccessful(status)) {
                    LogUtil.d(TAG, "onResponse: %s", status);
                    dataCallback.call(null, new HttpStatusException(response.header(Common.x_message) + "", status, reqUrl));
                    return;
                }
                dataCallback.call(jsonObject, null);
            }
        });
    }

    public void cloudVersions(DataCallback dataCallback) {
        Map<String, Object> object = new HashMap<>();
        String reqUrl = "/versions";
        object.put("method", "POST");
        object.put("url", reqUrl);

        Map<String, Object> headers = new HashMap<>();
        headers.put("User-Agent", userAgent);
        headers.put(cookie_key, SessionManager.getSession());
        object.put("headers", headers);

        Map<String, Object> data = new HashMap<>();
        object.put("data", data);

        String s = JsonUtil.toJson(object);
        LogUtil.d(TAG, "cloud versions request: %s : %s", reqUrl, s);

        Request.Builder builder = new Request.Builder()
                .url(getXburl())
                .post(RequestBody.create(s, MediaType.parse("application/json")));
        setCommonHeader(builder);
        HttpHelper.getSyncClient().newCall(builder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                LogUtil.d(TAG, "onFailure: %s", e);
                dataCallback.call(null, e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    LogUtil.d(TAG, "onResponse: %s", response.code());
                    dataCallback.call(null, new HttpStatusException(response.header(Common.x_message) + "", response.code(), reqUrl));
                    return;
                }
                String string = response.body().string();
                LogUtil.d(TAG, "cloud versions response: %s", string);
                JsonNode jsonObject = JsonUtil.readTree(string);
                int status = jsonObject.get("status").asInt();
                if (!Common.statusSuccessful(status)) {
                    LogUtil.d(TAG, "onResponse: %s", status);
                    dataCallback.call(null, new HttpStatusException(response.header(Common.x_message) + "", status, reqUrl));
                    return;
                }
                dataCallback.call(jsonObject, null);
            }
        });
    }

    public void cloudIssues(String title, String body, DataCallback dataCallback) {
        Map<String, Object> object = new HashMap<>();
        String reqUrl = "/issues";
        object.put("method", "POST");
        object.put("url", reqUrl);

        Map<String, Object> headers = new HashMap<>();
        headers.put("User-Agent", userAgent);
        headers.put(cookie_key, SessionManager.getSession());
        object.put("headers", headers);

        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("body", body);
        object.put("data", data);

        String s = JsonUtil.toJson(object);
        LogUtil.d(TAG, "cloud issues request: %s : %s", reqUrl, s);

        Request.Builder builder = new Request.Builder()
                .url(getXburl())
                .post(RequestBody.create(s, MediaType.parse("application/json")));
        setCommonHeader(builder);
        HttpHelper.getSyncClient().newCall(builder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                LogUtil.d(TAG, "onFailure: %s", e);
                dataCallback.call(null, e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    LogUtil.d(TAG, "onResponse: %s", response.code());
                    dataCallback.call(null, new HttpStatusException(response.header(Common.x_message) + "", response.code(), reqUrl));
                    return;
                }
                String string = response.body().string();
                LogUtil.d(TAG, "cloud issues response: %s", string);
                JsonNode jsonObject = JsonUtil.readTree(string);
                int status = jsonObject.get("status").asInt();
                if (!Common.statusSuccessful(status)) {
                    LogUtil.d(TAG, "onResponse: %s", status);
                    dataCallback.call(null, new HttpStatusException(response.header(Common.x_message) + "", status, reqUrl));
                    return;
                }
                dataCallback.call(jsonObject, null);
            }
        });
    }

    public void resetpwd(String email, String password, String code, DataCallback<JsonNode> dataCallback) {
        Map<String, Object> object = new HashMap<>();
        String reqUrl = "/zlib_resetpwd";
        object.put("method", "POST");
        object.put("url", reqUrl);

        Map<String, Object> headers = new HashMap<>();
        headers.put("User-Agent", userAgent);
        headers.put(content_type_key, "application/json");
        object.put("headers", headers);

        Map<String, Object> params = new HashMap<>();
        object.put("params", params);

        Map<String, Object> data = new HashMap<>();
        data.put("email", email);
        data.put("password", password);
        data.put("code", code);
        object.put("data", data);

        String s = JsonUtil.toJson(object);
        LogUtil.d(TAG, "resetpwd request: %s : %s", reqUrl, s);

        Request.Builder builder = new Request.Builder()
                .url(getXburl())
                .post(RequestBody.create(s, MediaType.parse("application/json")));
        setCommonHeader(builder);
        HttpHelper.getClient().newCall(builder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                LogUtil.d(TAG, "onFailure: %s", LogUtil.getStackTraceString(e));
                dataCallback.call(null, e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    LogUtil.d(TAG, "onResponse: %s", response.code());
                    dataCallback.call(null, new HttpStatusException(response.header(Common.x_message) + "", response.code(), reqUrl));
                    return;
                }
                String string = response.body().string();
                LogUtil.d(TAG, "resetpwd response: %s", string);
                JsonNode jsonObject = JsonUtil.readTree(string);
                int status = jsonObject.get("status").asInt();
                if (!Common.statusSuccessful(status)) {
                    LogUtil.d(TAG, "onResponse: %s", status);
                    dataCallback.call(null, new HttpStatusException(response.header(Common.x_message) + "", status, reqUrl));
                    return;
                }
                JsonNode data = jsonObject.get("data");
                dataCallback.call(data, null);
            }
        });
    }

    public void detailSuggest(String detailUrl, DataCallback<List<Book>> dataCallback) {
        Map<String, Object> object = new HashMap<>();
        String reqUrl = "/zlib_detail_suggest";
        object.put("method", "POST");
        object.put("url", reqUrl);

        Map<String, Object> headers = new HashMap<>();
        headers.put("User-Agent", userAgent);
        headers.put(cookie_key, SessionManager.getSession());
        object.put("headers", headers);

        Map<String, Object> params = new HashMap<>();
        object.put("params", params);

        Map<String, Object> data = new HashMap<>();
        data.put("detail_url", detailUrl);
        object.put("data", data);

        String s = JsonUtil.toJson(object);
        LogUtil.d(TAG, "detail suggest request: %s", s);

        Request.Builder builder = new Request.Builder()
                .url(getXburl())
                .post(RequestBody.create(s, MediaType.parse("application/json")));
        setCommonHeader(builder);
        HttpHelper.getClient().newCall(builder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                LogUtil.d(TAG, "onFailure: %s", LogUtil.getStackTraceString(e));
                dataCallback.call(null, e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    LogUtil.d(TAG, "onResponse: %s", response.code());
                    dataCallback.call(null, new HttpStatusException(response.header(Common.x_message) + "", response.code(), reqUrl));
                    return;
                }
                String string = response.body().string();
                LogUtil.d(TAG, "detail suggest response: %s", string);
                JsonNode jsonObject = JsonUtil.readTree(string);
                int status = jsonObject.get("status").asInt();
                if (!Common.statusSuccessful(status)) {
                    LogUtil.d(TAG, "onResponse: %s", status);
                    dataCallback.call(null, new HttpStatusException(response.header(Common.x_message) + "", status, reqUrl));
                    return;
                }
                JsonNode list = jsonObject.get("data").get("list");
                List<Book> books = JsonUtil.convertValue(list, new TypeReference<List<Book>>() {
                });
                dataCallback.call(books, null);
            }
        });
    }

    void setCommonHeader(Request.Builder builder) {
        builder.header(Common.header_vc, UiUtils.getVersionCode() + "")
                .header(Common.header_vn, UiUtils.getVersionName())
                .header(Common.header_platform, Common.platform_android)
                .header(Common.header_sv, android.os.Build.VERSION.RELEASE);
    }

}
