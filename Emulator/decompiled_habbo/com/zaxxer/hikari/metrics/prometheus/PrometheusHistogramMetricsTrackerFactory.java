/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.prometheus.client.CollectorRegistry
 */
package com.zaxxer.hikari.metrics.prometheus;

import com.zaxxer.hikari.metrics.IMetricsTracker;
import com.zaxxer.hikari.metrics.MetricsTrackerFactory;
import com.zaxxer.hikari.metrics.PoolStats;
import com.zaxxer.hikari.metrics.prometheus.HikariCPCollector;
import com.zaxxer.hikari.metrics.prometheus.PrometheusHistogramMetricsTracker;
import io.prometheus.client.CollectorRegistry;

public class PrometheusHistogramMetricsTrackerFactory
implements MetricsTrackerFactory {
    private HikariCPCollector collector;
    private CollectorRegistry collectorRegistry;

    public PrometheusHistogramMetricsTrackerFactory() {
        this.collectorRegistry = CollectorRegistry.defaultRegistry;
    }

    public PrometheusHistogramMetricsTrackerFactory(CollectorRegistry collectorRegistry) {
        this.collectorRegistry = collectorRegistry;
    }

    @Override
    public IMetricsTracker create(String poolName, PoolStats poolStats) {
        this.getCollector().add(poolName, poolStats);
        return new PrometheusHistogramMetricsTracker(poolName, this.collectorRegistry);
    }

    private HikariCPCollector getCollector() {
        if (this.collector == null) {
            this.collector = (HikariCPCollector)new HikariCPCollector().register(this.collectorRegistry);
        }
        return this.collector;
    }
}

