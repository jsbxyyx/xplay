package com.github.jsbxyyx.xbook.common;

/**
 * @author jsbxyyx
 * @since 1.0
 */
public interface DataCallback<T> {

    void call(T t, Throwable err);

}
