package com.github.jsbxyyx.xbook.httpserver;

import com.github.jsbxyyx.xbook.common.Common;
import com.github.jsbxyyx.xbook.common.LogUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * @author jsbxyyx
 */

public class HttpServer extends NanoHTTPD {
    private static final String TAG = "HttpServer";
    private MediaTypeFactory mediaTypeFactory;

    class ResourceResponse extends Response {
        ResourceResponse(String mimeType, InputStream data, long totalBytes) {
            super(Response.Status.OK, mimeType, data, totalBytes);
        }
    }

    public HttpServer(int port, MediaTypeFactory mediaTypeFactory) {
        super(port);
        this.mediaTypeFactory = mediaTypeFactory;
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        Method method = session.getMethod();
        Map<String, String> header = session.getHeaders();
        Map<String, List<String>> params = session.getParameters();
        String answer = "Success!";
        LogUtil.d(TAG, "uri=%s", uri);
        LogUtil.d(TAG, "method=%s", method);
        LogUtil.d(TAG, "header=%s", header);
        LogUtil.d(TAG, "params=%s", params);

        if (method.equals(Method.GET)) {
            File rootFile = new File(Common.xbook_dir);
            rootFile = new File(rootFile + uri);
            if (!rootFile.exists()) {
                return newFixedLengthResponse("Error! No such file or directory");
            }
            if (rootFile.isDirectory()) {
                // list directory files
                LogUtil.d(TAG, "list %s", rootFile.getPath());
                File[] files = rootFile.listFiles();
                answer = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; " +
                        "charset=utf-8\"><title> HTTP File Browser</title>";
                if (files != null && files.length > 0) {
                    for (File file : files) {
                        String name = file.getAbsolutePath().replace(Common.xbook_dir + "/", "");
                        String href = Common.urlEncode(name);
                        answer += "<a href=\"" + href + "\" alt = \"\">" + name + "</a><br>";
                    }
                }
                answer += "</head></html>";
            } else {
                String name = rootFile.getName();
                byte[] bytes = null;
                try {
                    bytes = Files.readAllBytes(rootFile.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayInputStream in;
                if (name.endsWith(".js")
                        || name.endsWith(".css")
                        || name.endsWith(".htm")
                        || name.endsWith(".html")) {
                    in = new ByteArrayInputStream(bytes);
                } else {
                    ByteBuffer head = ByteBuffer.allocate(8);
                    head.put(bytes, 0, 8);
                    head.flip();
                    long magic = head.getLong();
                    long m = magic ^ Common.MG_XOR;
                    if (m == Common.MAGIC) {
                        byte[] newBytes = new byte[bytes.length - 8];
                        System.arraycopy(bytes, 8, newBytes, 0, bytes.length - 8);
                        in = new ByteArrayInputStream(Common.xor(newBytes, newBytes.length, Common.MG_XOR));
                        LogUtil.d(TAG, "mg x");
                    } else if (m == (Common.MAGIC ^ Common.MG_XOR)) {
                        byte[] newBytes = new byte[bytes.length - 8];
                        System.arraycopy(bytes, 8, newBytes, 0, bytes.length - 8);
                        in = new ByteArrayInputStream(newBytes, newBytes.length, Common.MG_XOR);
                        LogUtil.d(TAG, "mg nx");
                    } else {
                        in = new ByteArrayInputStream(bytes);
                        LogUtil.d(TAG, "mg n");
                    }
                }
                long totalBytes = in.available();
                LogUtil.d(TAG, "%s bytes : %s", name, totalBytes);
                return new ResourceResponse(mediaTypeFactory.getMediaTypes(name, "application/octet-stream"),
                        in, totalBytes);
            }
        }
        return newFixedLengthResponse(answer);
    }

}