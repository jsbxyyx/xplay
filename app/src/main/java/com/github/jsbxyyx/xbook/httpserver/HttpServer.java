package com.github.jsbxyyx.xbook.httpserver;

import com.github.jsbxyyx.xbook.common.Common;
import com.github.jsbxyyx.xbook.common.LogUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
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
                long totalBytes = rootFile.length();
                String name = rootFile.getName();
                FileInputStream inputStream = null;
                try {
                    inputStream = new FileInputStream(rootFile);

                    FileChannel channel = inputStream.getChannel();
                    ByteBuffer buf = ByteBuffer.allocate(8);
                    channel.read(buf);
                    buf.flip();
                    long magic = buf.getLong();
                    if (magic == Common.MAGIC) {
                        totalBytes -= 8;
                    } else {
                        channel.position(channel.position() - 8);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (inputStream == null) {
                    return newFixedLengthResponse(name + " not found");
                }
                return new ResourceResponse(mediaTypeFactory.getMediaTypes(name, "application/octet-stream"),
                        inputStream, totalBytes);
            }
        }
        return newFixedLengthResponse(answer);
    }

}