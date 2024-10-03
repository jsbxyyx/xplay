package com.github.jsbxyyx.xbook.common;

/**
 * @author jsbxyyx
 */
public interface ProgressListener {

    void onProgress(long bytesRead, long total);

}
