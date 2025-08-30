package com.github.jsbxyyx.xbook.httpserver;

import static com.github.jsbxyyx.xbook.common.UriUtils.encodeURIComponent;

import com.github.jsbxyyx.xbook.common.Common;
import com.github.jsbxyyx.xbook.common.LogUtil;
import com.github.jsbxyyx.xbook.common.UiUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * @author jsbxyyx
 */

public class FileHttpServer extends NanoHTTPD {
    private final String TAG = getClass().getSimpleName();
    private MediaTypeFactory mediaTypeFactory;

    class ResourceResponse extends Response {
        ResourceResponse(String mimeType, InputStream data, long totalBytes) {
            super(Response.Status.OK, mimeType, data, totalBytes);
        }
    }

    public FileHttpServer(int port, MediaTypeFactory mediaTypeFactory) {
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

        if (method.equals(Method.OPTIONS)) {
            Response response = newFixedLengthResponse(Response.Status.OK, MIME_PLAINTEXT, "");
            HttpServerUtils.cors(session, response);
            return response;
        }

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
                        String href = encodeURIComponent(name);
                        answer += "<a href=\"" + href + "\" alt = \"\">" + name + "</a><br>";
                    }
                }
                answer += "</head></html>";
            } else {
                try {
                    long totalBytes = 0L;
                    String name = rootFile.getName();
                    InputStream in;
                    if (name.endsWith(".js")
                            || name.endsWith(".css")
                            || name.endsWith(".htm")
                            || name.endsWith(".html")
                            || name.endsWith(".bcmap")
                            || name.endsWith(".map")) {
                        LogUtil.d(TAG, "ignore : %s", name);
                        in = new FileInputStream(rootFile);
                        totalBytes = in.available();
                    } else {
                        ByteBuffer head = ByteBuffer.allocate(8);
                        try {
                            FileChannel c = FileChannel.open(rootFile.toPath());
                            totalBytes = c.size();
                            c.read(head);
                            head.flip();
                            c.close();
                        } catch (IOException e) {
                            LogUtil.e(TAG, "%s", LogUtil.getStackTraceString(e));
                            UiUtils.showToast("未开启文件管理权限，请前往APP-设置打开文件管理权限");
                        }

                        long magic = head.getLong();
                        long m = magic ^ Common.MG_XOR;
                        if (m == Common.MAGIC) {
                            totalBytes -= 8;
                            in = new XorInputStream(new FileInputStream(rootFile), 8, Common.MG_XOR, 1);
                            LogUtil.d(TAG, "mg x");
                        } else if (m == (Common.MAGIC ^ Common.MG_XOR)) {
                            totalBytes -= 8;
                            in = new XorInputStream(new FileInputStream(rootFile), 8, Common.MG_XOR, 0);
                            LogUtil.d(TAG, "mg nx");
                        } else {
                            in = new FileInputStream(rootFile);
                            LogUtil.d(TAG, "mg n");
                        }
                    }
                    LogUtil.d(TAG, "%s bytes : %s", name, totalBytes);
                    ResourceResponse response = new ResourceResponse(
                            mediaTypeFactory.getMediaTypes(name, "application/octet-stream"),
                            in,
                            totalBytes);
                    HttpServerUtils.cors(session, response);
                    return response;
                } catch (Exception e) {
                    LogUtil.e(TAG, "%s", LogUtil.getStackTraceString(e));
                }
                Response response = newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_HTML, "Not Found");
                HttpServerUtils.cors(session, response);
                return response;
            }
        }
        Response response = newFixedLengthResponse(answer);
        HttpServerUtils.cors(session, response);
        return response;
    }

}