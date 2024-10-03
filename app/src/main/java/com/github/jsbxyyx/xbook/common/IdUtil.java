package com.github.jsbxyyx.xbook.common;

import java.time.Instant;
import java.util.SplittableRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jsbxyyx
 */
public class IdUtil {

    private static final int RANDOM_BITS = 22;
    private static final int RANDOM_MASK = 0x003fffff;
    private static final long ID_EPOCH = Instant.parse("2024-01-01T00:00:00.000Z").toEpochMilli();
    private static final AtomicInteger counter = new AtomicInteger((new SplittableRandom()).nextInt());

    public static long nextId() {
        final long time = (System.currentTimeMillis() - ID_EPOCH) << RANDOM_BITS;
        final long tail = counter.incrementAndGet() & RANDOM_MASK;
        return (time | tail);
    }

}