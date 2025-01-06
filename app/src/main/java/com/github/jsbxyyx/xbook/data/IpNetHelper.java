package com.github.jsbxyyx.xbook.data;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.jsbxyyx.xbook.common.Common;
import com.github.jsbxyyx.xbook.common.DataCallback;
import com.github.jsbxyyx.xbook.common.HttpStatusException;
import com.github.jsbxyyx.xbook.common.JsonUtil;
import com.github.jsbxyyx.xbook.common.LogUtil;
import com.github.jsbxyyx.xbook.data.bean.Ip;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class IpNetHelper {

    private static final String TAG = "IpNetHelper";

    public void fetchIP(DataCallback<List<Ip>> dataCallback) {
        final String reqUrl = "/ip";
        Request.Builder builder = new Request.Builder()
                .url("https://http1.idingdang.org" + reqUrl)
                .get();
        LogUtil.d(TAG, "fetch ip request: %s", reqUrl);
        HttpHelper.getDnsClient().newCall(builder.build()).enqueue(new Callback() {

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
                LogUtil.d(TAG, "fetch ip response: %s", string);
                JsonNode jsonObject = JsonUtil.readTree(string);
                int status = jsonObject.get("status").asInt();
                if (!Common.statusSuccessful(status)) {
                    LogUtil.d(TAG, "onResponse: %s", status);
                    dataCallback.call(new ArrayList<>(), new HttpStatusException(response.header(Common.x_message) + "", status, reqUrl));
                    return;
                }
                JsonNode data = jsonObject.get("data");
                JsonNode v4 = data.get("v4");
                if (v4 == null) {
                    LogUtil.d(TAG, "v4 not found: %s", data);
                    dataCallback.call(new ArrayList<>(), new HttpStatusException(response.header(Common.x_message) + "", status, reqUrl));
                    return;
                }
                JsonNode cm = v4.get("CM");
                JsonNode cu = v4.get("CU");
                JsonNode ct = v4.get("CT");
                List<Ip> ips = new ArrayList<>(10);
                if (cm != null) {
                    for (int i = 0; i < cm.size(); i++) {
                        JsonNode node = cm.get(i);
                        Ip ip = new Ip();
                        if (node.has("ip") && !node.get("ip").asText().isBlank()) {
                            ip.setName(node.get("name").asText());
                            ip.setIp(node.get("ip").asText());
                            ip.setColo(node.get("colo").asText());
                            ip.setLatency(node.get("latency").asText());
                            ip.setSpeed(node.get("speed").asText());
                            ip.setUptime(node.get("uptime").asText());
                            ips.add(ip);
                        }
                    }
                }
                if (cu != null) {
                    for (int i = 0; i < cu.size(); i++) {
                        JsonNode node = cu.get(i);
                        Ip ip = new Ip();
                        if (node.has("ip") && !node.get("ip").asText().isBlank()) {
                            ip.setName(node.get("name").asText());
                            ip.setIp(node.get("ip").asText());
                            ip.setColo(node.get("colo").asText());
                            ip.setLatency(node.get("latency").asText());
                            ip.setSpeed(node.get("speed").asText());
                            ip.setUptime(node.get("uptime").asText());
                            ips.add(ip);
                        }
                    }
                }
                if (ct != null) {
                    for (int i = 0; i < ct.size(); i++) {
                        JsonNode node = ct.get(i);
                        Ip ip = new Ip();
                        if (node.has("ip") && !node.get("ip").asText().isBlank()) {
                            ip.setName(node.get("name").asText());
                            ip.setIp(node.get("ip").asText());
                            ip.setColo(node.get("colo").asText());
                            ip.setLatency(node.get("latency").asText());
                            ip.setSpeed(node.get("speed").asText());
                            ip.setUptime(node.get("uptime").asText());
                            ips.add(ip);
                        }
                    }
                }
                dataCallback.call(ips, null);
            }
        });
    }

}
