/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.core.instrument.MeterRegistry
 */
package com.zaxxer.hikari.metrics.micrometer;

import com.zaxxer.hikari.metrics.IMetricsTracker;
import com.zaxxer.hikari.metrics.MetricsTrackerFactory;
import com.zaxxer.hikari.metrics.PoolStats;
import com.zaxxer.hikari.metrics.micrometer.MicrometerMetricsTracker;
import io.micrometer.core.instrument.MeterRegistry;

public class MicrometerMetricsTrackerFactory
implements MetricsTrackerFactory {
    private final MeterRegistry registry;

    public MicrometerMetricsTrackerFactory(MeterRegistry registry) {
        this.registry = registry;
    }

    @Override
    public IMetricsTracker create(String poolName, PoolStats poolStats) {
        return new MicrometerMetricsTracker(poolName, poolStats, this.registry);
    }
}

