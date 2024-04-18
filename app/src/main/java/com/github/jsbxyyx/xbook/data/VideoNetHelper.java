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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author jsbxyyx
 */
public class VideoNetHelper {

    private OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(5000, TimeUnit.MILLISECONDS)
            .readTimeout(30000, TimeUnit.MILLISECONDS)
            .writeTimeout(30000, TimeUnit.MILLISECONDS)
            .build();

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
        LogUtil.d(TAG, "search request: %s", s);
        Request request = new Request.Builder()
                .url(xburl)
                .post(RequestBody.create(s, MediaType.parse("application/json")))
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                LogUtil.d(TAG, "onFailure: %s", LogUtil.getStackTraceString(e));
                dataCallback.call(new ArrayList<>(), e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    dataCallback.call(new ArrayList<>(), new HttpStatusException(response.code() + "", response.code(), reqUrl));
                    return;
                }
                String string = response.body().string();
                LogUtil.d(TAG, "search response: %s", string);
                try {
                    JsonNode jsonObject = JsonUtil.readTree(string);
                    int status = jsonObject.get("status").asInt();
                    if (!Common.statusSuccessful(status)) {
                        dataCallback.call(new ArrayList<>(), new HttpStatusException(status + "", status, reqUrl));
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

}
