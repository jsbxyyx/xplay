package com.github.jsbxyyx.xbook.common;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jsbxyyx
 */
public class ThreadUtils {

    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors(),
            60000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(100),
            new ThreadFactory() {
                private final AtomicInteger count = new AtomicInteger();

                @Override
                public Thread newThread(Runnable runnable) {
                    return new Thread(runnable, "biz #" + count.getAndIncrement());
                }
            }, new ThreadPoolExecutor.CallerRunsPolicy());

    public static void submit(Runnable task) {
        executor.submit(task);
    }


}
