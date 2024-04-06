package com.github.jsbxyyx.xbook.httpserver;

import android.content.Context;

import com.github.jsbxyyx.xbook.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * @author jsbxyyx
 */
public class MediaTypeFactory {

    private static Map<String, String> fileExtensionToMediaTypes;
    private Context context;

    public MediaTypeFactory(Context context) {
        this.context = context;
        fileExtensionToMediaTypes = parseMimeTypes();
    }

    private Map<String, String> parseMimeTypes() {
        InputStream is = context.getResources().openRawResource(R.raw.mime);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.US_ASCII))) {
            Map<String, String> result = new LinkedHashMap<>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty() || line.charAt(0) == '#') {
                    continue;
                }
                String[] tokens = tokenizeToStringArray(line, " \t\n\r\f", true, true);
                for (int i = 1; i < tokens.length; i++) {
                    String fileExtension = tokens[i].toLowerCase(Locale.ENGLISH);
                    result.put(fileExtension, tokens[0]);
                }
            }
            return result;
        } catch (IOException ex) {
            throw new IllegalStateException("Could not read mime.types", ex);
        }
    }

    public String getMediaTypes(String filename, String defaultMediaType) {
        String mediaTypes = null;
        String ext = getFilenameExtension(filename);
        if (ext != null) {
            mediaTypes = fileExtensionToMediaTypes.get(ext.toLowerCase(Locale.ENGLISH));
        }
        return (mediaTypes != null ? mediaTypes : defaultMediaType);
    }

    public static String getFilenameExtension(String path) {
        if (path == null) {
            return null;
        }

        int extIndex = path.lastIndexOf(".");
        if (extIndex == -1) {
            return null;
        }

        int folderIndex = path.lastIndexOf("/");
        if (folderIndex > extIndex) {
            return null;
        }

        return path.substring(extIndex + 1);
    }

    public static String[] tokenizeToStringArray(String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {
        if (str == null) {
            return new String[0];
        }
        StringTokenizer st = new StringTokenizer(str, delimiters);
        List<String> tokens = new ArrayList<>();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (trimTokens) {
                token = token.trim();
            }
            if (!ignoreEmptyTokens || token.length() > 0) {
                tokens.add(token);
            }
        }
        return tokens.toArray(new String[0]);
    }

}
