/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.prometheus.client.Collector
 *  io.prometheus.client.CollectorRegistry
 */
package com.zaxxer.hikari.metrics.prometheus;

import com.zaxxer.hikari.metrics.IMetricsTracker;
import com.zaxxer.hikari.metrics.MetricsTrackerFactory;
import com.zaxxer.hikari.metrics.PoolStats;
import com.zaxxer.hikari.metrics.prometheus.HikariCPCollector;
import com.zaxxer.hikari.metrics.prometheus.PrometheusMetricsTracker;
import io.prometheus.client.Collector;
import io.prometheus.client.CollectorRegistry;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PrometheusMetricsTrackerFactory
implements MetricsTrackerFactory {
    private static final Map<CollectorRegistry, RegistrationStatus> registrationStatuses = new ConcurrentHashMap<CollectorRegistry, RegistrationStatus>();
    private final HikariCPCollector collector = new HikariCPCollector();
    private final CollectorRegistry collectorRegistry;

    public PrometheusMetricsTrackerFactory() {
        this(CollectorRegistry.defaultRegistry);
    }

    public PrometheusMetricsTrackerFactory(CollectorRegistry collectorRegistry) {
        this.collectorRegistry = collectorRegistry;
    }

    @Override
    public IMetricsTracker create(String poolName, PoolStats poolStats) {
        this.registerCollector(this.collector, this.collectorRegistry);
        this.collector.add(poolName, poolStats);
        return new PrometheusMetricsTracker(poolName, this.collectorRegistry, this.collector);
    }

    private void registerCollector(Collector collector, CollectorRegistry collectorRegistry) {
        if (registrationStatuses.putIfAbsent(collectorRegistry, RegistrationStatus.REGISTERED) == null) {
            collector.register(collectorRegistry);
        }
    }

    public static enum RegistrationStatus {
        REGISTERED;

    }
}

