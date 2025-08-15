package com.github.jsbxyyx.xbook.tts;

import static com.github.jsbxyyx.xbook.common.Common.getTtsurl;

import androidx.annotation.NonNull;

import com.github.jsbxyyx.xbook.common.JsonUtil;
import com.github.jsbxyyx.xbook.data.HttpHelper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TTSClient {

    public static FutureResult audioByText(String text, String voice, String speed, String volume, String pitch, String style) {
        Map<String, Object> object = new HashMap<>();
        object.put("input", text);
        if (voice != null) {
            object.put("voice", voice);
        }
        if (speed != null) {
            object.put("speed", speed);
        }
        if (volume != null) {
            object.put("volume", volume);
        }
        if (pitch != null) {
            object.put("pitch", pitch);
        }
        if (style != null) {
            object.put("style", style);
        }
        String s = JsonUtil.toJson(object);
        Request.Builder builder = new Request.Builder()
                .url(getTtsurl())
                .header("x-api-key", "xxx")
                .post(RequestBody.create(s, MediaType.parse("application/json")));
        CompletableFuture<FutureResult> cf = new CompletableFuture<>();
        HttpHelper.getClient().newCall(builder.build()).enqueue(new Callback() {

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String header = response.header("content-type");
                if (header.startsWith("audio/")) {
                    byte[] bytes = response.body().bytes();
                    cf.complete(FutureResult.success(bytes));
                } else {
                    String string = response.body().string();
                    cf.complete(FutureResult.error(string));
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                cf.complete(FutureResult.error("{\"error\":{\"message\":\"tts request failure\",\"type\":\"client_error\"}}"));
            }
        });
        try {
            return cf.get(15000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            return FutureResult.error("{\"error\":{\"message\":\"request timeout\",\"type\":\"client_error\"}}");
        }
    }

}
