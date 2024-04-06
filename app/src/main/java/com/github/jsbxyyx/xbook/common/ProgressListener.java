package com.github.jsbxyyx.xbook.common;

/**
 * @author jsbxyyx
 * @since 1.0
 */
public interface ProgressListener {

    void onProgress(long bytesRead, long total);

}
