/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LatencyTracker {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameClient.class);
    private boolean initialPing = true;
    private long last;
    private long average = 0L;

    public void update(long latencyInNano) {
        this.last = latencyInNano;
        if (this.initialPing) {
            this.initialPing = false;
            this.average = latencyInNano;
            return;
        }
        this.average = (long)((float)this.average * 0.7f + (float)latencyInNano * 0.3f);
    }

    public boolean hasInitialized() {
        return !this.initialPing;
    }

    public long getLastMs() {
        return TimeUnit.NANOSECONDS.toMillis(this.last);
    }

    public long getAverageMs() {
        return TimeUnit.NANOSECONDS.toMillis(this.average);
    }
}

