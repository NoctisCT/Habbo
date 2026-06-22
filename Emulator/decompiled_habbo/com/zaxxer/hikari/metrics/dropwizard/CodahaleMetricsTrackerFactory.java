/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.codahale.metrics.MetricRegistry
 */
package com.zaxxer.hikari.metrics.dropwizard;

import com.codahale.metrics.MetricRegistry;
import com.zaxxer.hikari.metrics.IMetricsTracker;
import com.zaxxer.hikari.metrics.MetricsTrackerFactory;
import com.zaxxer.hikari.metrics.PoolStats;
import com.zaxxer.hikari.metrics.dropwizard.CodaHaleMetricsTracker;

public final class CodahaleMetricsTrackerFactory
implements MetricsTrackerFactory {
    private final MetricRegistry registry;

    public CodahaleMetricsTrackerFactory(MetricRegistry registry) {
        this.registry = registry;
    }

    public MetricRegistry getRegistry() {
        return this.registry;
    }

    @Override
    public IMetricsTracker create(String poolName, PoolStats poolStats) {
        return new CodaHaleMetricsTracker(poolName, poolStats, this.registry);
    }
}

