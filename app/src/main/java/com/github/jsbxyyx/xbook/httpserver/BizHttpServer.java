package com.github.jsbxyyx.xbook.httpserver;

import android.content.Context;

import com.github.jsbxyyx.xbook.common.Common;
import com.github.jsbxyyx.xbook.common.JsonUtil;
import com.github.jsbxyyx.xbook.common.LogUtil;
import com.github.jsbxyyx.xbook.common.SessionManager;
import com.github.jsbxyyx.xbook.common.UiUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class BizHttpServer extends NanoHTTPD {


    private final String TAG = getClass().getSimpleName();
    private Context mContext;

    public BizHttpServer(int port, Context context) {
        super(port);
        mContext = context;
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        Method method = session.getMethod();
        Map<String, String> header = session.getHeaders();
        Map<String, List<String>> params = session.getParameters();

        LogUtil.d(TAG, "uri=%s | method=%s | header=%s | params=%s", uri, method, header, params);

        if (method.equals(Method.OPTIONS)) {
            Response response = newFixedLengthResponse(Response.Status.OK, MIME_PLAINTEXT, "{}");
            HttpServerUtils.cors(session, response);
            return response;
        }

        if (method.equals(Method.GET)) {

            if ("/suid".equals(uri)) {
                String s = SessionManager.getSession();
                Map<String, String> kvMap = Common.parseKv(s);
                String uid = kvMap.getOrDefault(Common.serv_userid, "");
                Map<String, Object> map = new HashMap<>();
                map.put("uid", uid);
                String json = JsonUtil.toJson(map);

                Response response = newFixedLengthResponse(Response.Status.OK, MIME_PLAINTEXT, json);
                HttpServerUtils.cors(session, response);
                return response;
            }

            if ("/versions".equals(uri)) {
                int vc = UiUtils.getVersionCode();
                String vn = UiUtils.getVersionName();
                Map<String, Object> map = new HashMap<>();
                map.put("vc", vc + "");
                map.put("vn", vn);
                String json = JsonUtil.toJson(map);

                Response response = newFixedLengthResponse(Response.Status.OK, MIME_PLAINTEXT, json);
                HttpServerUtils.cors(session, response);
                return response;
            }

        }
        Response response = newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "{}");
        HttpServerUtils.cors(session, response);
        return response;
    }

}
