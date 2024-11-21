package com.github.jsbxyyx.xbook.httpserver;

import com.github.jsbxyyx.xbook.common.Common;

import fi.iki.elonen.NanoHTTPD;

public class HttpServerUtils {

    public static void cors(NanoHTTPD.IHTTPSession session, NanoHTTPD.Response response) {
        String origin = session.getHeaders().get("origin");
        response.addHeader("Access-Control-Allow-Origin", Common.isBlank(origin) ? "*" : origin);
        response.addHeader("Access-Control-Allow-Credentials", "true");
        response.addHeader("Access-Control-Max-Age", "86400");
        response.addHeader("Access-Control-Expose-Headers", "tk");
        response.addHeader("Access-Control-Allow-Headers", "tk, Content-Type, X-Requested-With");
        response.addHeader("Access-Control-Allow-Methods", "HEAD, POST, GET, PUT, DELETE, OPTIONS");
        response.addHeader("Vary", "Origin, Access-Control-Request-Method, Access-Control-Request-Headers");
    }

}
