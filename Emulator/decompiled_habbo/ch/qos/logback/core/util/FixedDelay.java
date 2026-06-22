/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.util;

import ch.qos.logback.core.util.DelayStrategy;

public class FixedDelay
implements DelayStrategy {
    private final long subsequentDelay;
    private long nextDelay;

    public FixedDelay(long initialDelay, long subsequentDelay) {
        this.nextDelay = initialDelay;
        this.subsequentDelay = subsequentDelay;
    }

    public FixedDelay(int delay) {
        this(delay, delay);
    }

    @Override
    public long nextDelay() {
        long delay = this.nextDelay;
        this.nextDelay = this.subsequentDelay;
        return delay;
    }
}

