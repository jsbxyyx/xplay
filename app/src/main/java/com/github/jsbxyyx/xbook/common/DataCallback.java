package com.github.jsbxyyx.xbook.common;

/**
 * @author jsbxyyx
 */
public interface DataCallback<T> {

    void call(T t, Throwable err);

    public static class NopDataCallback implements DataCallback {
        @Override
        public void call(Object o, Throwable err) {
            if (err != null) {
                LogUtil.e(getClass().getSimpleName(), "Nop data callback err. %s", LogUtil.getStackTraceString(err));
            }
            LogUtil.d(getClass().getSimpleName(), "Nop data callback. %s", o);
        }
    }

}
