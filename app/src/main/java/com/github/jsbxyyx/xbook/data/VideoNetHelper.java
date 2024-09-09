package com.github.jsbxyyx.xbook.data;

import static com.github.jsbxyyx.xbook.common.Common.xburl;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.jsbxyyx.xbook.common.Common;
import com.github.jsbxyyx.xbook.common.DataCallback;
import com.github.jsbxyyx.xbook.common.HttpStatusException;
import com.github.jsbxyyx.xbook.common.JsonUtil;
import com.github.jsbxyyx.xbook.common.LogUtil;
import com.github.jsbxyyx.xbook.data.bean.QqVideo;
import com.github.jsbxyyx.xbook.data.bean.QqVideoHotRank;
import com.github.jsbxyyx.xbook.data.bean.QqVideoHotWord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author jsbxyyx
 */
public class VideoNetHelper {

    private String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36";

    private String TAG = getClass().getSimpleName();

    public void search(String q, DataCallback dataCallback) {
        Map<String, Object> object = new HashMap<>();

        String reqUrl = "/vqq";
        object.put("method", "GET");
        object.put("url", reqUrl);

        Map<String, Object> headers = new HashMap<>();
        headers.put("User-Agent", userAgent);
        object.put("headers", headers);

        List<Object> params = new ArrayList<>();
        List<String> p0 = new ArrayList<>();
        p0.add("q");
        p0.add(q);
        params.add(p0);
        object.put("params", params);

        String s = JsonUtil.toJson(object);
        LogUtil.d(TAG, "search request: %s : %s", reqUrl, s);
        Request request = new Request.Builder()
                .url(xburl)
                .post(RequestBody.create(s, MediaType.parse("application/json")))
                .build();
        HttpHelper.getClient().newCall(request).enqueue(new Callback() {
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
                    List<QqVideo> list = JsonUtil.convertValue(data.get("list"), new TypeReference<List<QqVideo>>() {
                    });
                    dataCallback.call(list, null);
                } catch (Exception e) {
                    dataCallback.call(new ArrayList<>(), e);
                }
            }
        });
    }

    public void hotRank(DataCallback dataCallback) {
        Map<String, Object> object = new HashMap<>();

        String reqUrl = "/hotrank_vqq";
        object.put("method", "POST");
        object.put("url", reqUrl);

        Map<String, Object> headers = new HashMap<>();
        headers.put("User-Agent", userAgent);
        object.put("headers", headers);

        Map<String, Object> params = new HashMap<>();
        object.put("params", params);

        String s = JsonUtil.toJson(object);
        LogUtil.d(TAG, "hotrank request: %s : %s", reqUrl, s);
        Request request = new Request.Builder()
                .url(xburl)
                .post(RequestBody.create(s, MediaType.parse("application/json")))
                .build();
        HttpHelper.getClient().newCall(request).enqueue(new Callback() {
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
                LogUtil.d(TAG, "hotrank response: %s", string);
                try {
                    JsonNode jsonObject = JsonUtil.readTree(string);
                    int status = jsonObject.get("status").asInt();
                    if (!Common.statusSuccessful(status)) {
                        dataCallback.call(new ArrayList<>(), new HttpStatusException(response.header(Common.x_message) + "", status, reqUrl));
                        return;
                    }
                    JsonNode data = jsonObject.get("data");
                    List<QqVideoHotRank> list = JsonUtil.convertValue(data.get("list"), new TypeReference<List<QqVideoHotRank>>() {
                    });
                    dataCallback.call(list, null);
                } catch (Exception e) {
                    dataCallback.call(new ArrayList<>(), e);
                }
            }
        });
    }

    public void hotWord(DataCallback dataCallback) {
        Map<String, Object> object = new HashMap<>();

        String reqUrl = "/hotwordlist_vqq";
        object.put("method", "POST");
        object.put("url", reqUrl);

        Map<String, Object> headers = new HashMap<>();
        headers.put("User-Agent", userAgent);
        object.put("headers", headers);

        Map<String, Object> params = new HashMap<>();
        object.put("params", params);

        String s = JsonUtil.toJson(object);
        LogUtil.d(TAG, "hotrank request: %s : %s", reqUrl, s);
        Request request = new Request.Builder()
                .url(xburl)
                .post(RequestBody.create(s, MediaType.parse("application/json")))
                .build();
        HttpHelper.getClient().newCall(request).enqueue(new Callback() {
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
                LogUtil.d(TAG, "hotrank response: %s", string);
                try {
                    JsonNode jsonObject = JsonUtil.readTree(string);
                    int status = jsonObject.get("status").asInt();
                    if (!Common.statusSuccessful(status)) {
                        dataCallback.call(new ArrayList<>(), new HttpStatusException(response.header(Common.x_message) + "", status, reqUrl));
                        return;
                    }
                    JsonNode data = jsonObject.get("data");
                    List<QqVideoHotWord> list = JsonUtil.convertValue(data.get("list"), new TypeReference<List<QqVideoHotWord>>() {
                    });
                    dataCallback.call(list, null);
                } catch (Exception e) {
                    dataCallback.call(new ArrayList<>(), e);
                }
            }
        });
    }

}
