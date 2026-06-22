/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.codahale.metrics.Gauge
 *  com.codahale.metrics.Histogram
 *  com.codahale.metrics.Meter
 *  com.codahale.metrics.Metric
 *  com.codahale.metrics.MetricRegistry
 *  com.codahale.metrics.Timer
 */
package com.zaxxer.hikari.metrics.dropwizard;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.zaxxer.hikari.metrics.IMetricsTracker;
import com.zaxxer.hikari.metrics.PoolStats;
import java.util.concurrent.TimeUnit;

public final class CodaHaleMetricsTracker
implements IMetricsTracker {
    private final String poolName;
    private final Timer connectionObtainTimer;
    private final Histogram connectionUsage;
    private final Histogram connectionCreation;
    private final Meter connectionTimeoutMeter;
    private final MetricRegistry registry;
    private static final String METRIC_CATEGORY = "pool";
    private static final String METRIC_NAME_WAIT = "Wait";
    private static final String METRIC_NAME_USAGE = "Usage";
    private static final String METRIC_NAME_CONNECT = "ConnectionCreation";
    private static final String METRIC_NAME_TIMEOUT_RATE = "ConnectionTimeoutRate";
    private static final String METRIC_NAME_TOTAL_CONNECTIONS = "TotalConnections";
    private static final String METRIC_NAME_IDLE_CONNECTIONS = "IdleConnections";
    private static final String METRIC_NAME_ACTIVE_CONNECTIONS = "ActiveConnections";
    private static final String METRIC_NAME_PENDING_CONNECTIONS = "PendingConnections";
    private static final String METRIC_NAME_MAX_CONNECTIONS = "MaxConnections";
    private static final String METRIC_NAME_MIN_CONNECTIONS = "MinConnections";

    CodaHaleMetricsTracker(String poolName, PoolStats poolStats, MetricRegistry registry) {
        this.poolName = poolName;
        this.registry = registry;
        this.connectionObtainTimer = registry.timer(MetricRegistry.name((String)poolName, (String[])new String[]{METRIC_CATEGORY, METRIC_NAME_WAIT}));
        this.connectionUsage = registry.histogram(MetricRegistry.name((String)poolName, (String[])new String[]{METRIC_CATEGORY, METRIC_NAME_USAGE}));
        this.connectionCreation = registry.histogram(MetricRegistry.name((String)poolName, (String[])new String[]{METRIC_CATEGORY, METRIC_NAME_CONNECT}));
        this.connectionTimeoutMeter = registry.meter(MetricRegistry.name((String)poolName, (String[])new String[]{METRIC_CATEGORY, METRIC_NAME_TIMEOUT_RATE}));
        registry.register(MetricRegistry.name((String)poolName, (String[])new String[]{METRIC_CATEGORY, METRIC_NAME_TOTAL_CONNECTIONS}), (Metric)((Gauge)poolStats::getTotalConnections));
        registry.register(MetricRegistry.name((String)poolName, (String[])new String[]{METRIC_CATEGORY, METRIC_NAME_IDLE_CONNECTIONS}), (Metric)((Gauge)poolStats::getIdleConnections));
        registry.register(MetricRegistry.name((String)poolName, (String[])new String[]{METRIC_CATEGORY, METRIC_NAME_ACTIVE_CONNECTIONS}), (Metric)((Gauge)poolStats::getActiveConnections));
        registry.register(MetricRegistry.name((String)poolName, (String[])new String[]{METRIC_CATEGORY, METRIC_NAME_PENDING_CONNECTIONS}), (Metric)((Gauge)poolStats::getPendingThreads));
        registry.register(MetricRegistry.name((String)poolName, (String[])new String[]{METRIC_CATEGORY, METRIC_NAME_MAX_CONNECTIONS}), (Metric)((Gauge)poolStats::getMaxConnections));
        registry.register(MetricRegistry.name((String)poolName, (String[])new String[]{METRIC_CATEGORY, METRIC_NAME_MIN_CONNECTIONS}), (Metric)((Gauge)poolStats::getMinConnections));
    }

    @Override
    public void close() {
        this.registry.remove(MetricRegistry.name((String)this.poolName, (String[])new String[]{METRIC_CATEGORY, METRIC_NAME_WAIT}));
        this.registry.remove(MetricRegistry.name((String)this.poolName, (String[])new String[]{METRIC_CATEGORY, METRIC_NAME_USAGE}));
        this.registry.remove(MetricRegistry.name((String)this.poolName, (String[])new String[]{METRIC_CATEGORY, METRIC_NAME_CONNECT}));
        this.registry.remove(MetricRegistry.name((String)this.poolName, (String[])new String[]{METRIC_CATEGORY, METRIC_NAME_TIMEOUT_RATE}));
        this.registry.remove(MetricRegistry.name((String)this.poolName, (String[])new String[]{METRIC_CATEGORY, METRIC_NAME_TOTAL_CONNECTIONS}));
        this.registry.remove(MetricRegistry.name((String)this.poolName, (String[])new String[]{METRIC_CATEGORY, METRIC_NAME_IDLE_CONNECTIONS}));
        this.registry.remove(MetricRegistry.name((String)this.poolName, (String[])new String[]{METRIC_CATEGORY, METRIC_NAME_ACTIVE_CONNECTIONS}));
        this.registry.remove(MetricRegistry.name((String)this.poolName, (String[])new String[]{METRIC_CATEGORY, METRIC_NAME_PENDING_CONNECTIONS}));
        this.registry.remove(MetricRegistry.name((String)this.poolName, (String[])new String[]{METRIC_CATEGORY, METRIC_NAME_MAX_CONNECTIONS}));
        this.registry.remove(MetricRegistry.name((String)this.poolName, (String[])new String[]{METRIC_CATEGORY, METRIC_NAME_MIN_CONNECTIONS}));
    }

    @Override
    public void recordConnectionAcquiredNanos(long elapsedAcquiredNanos) {
        this.connectionObtainTimer.update(elapsedAcquiredNanos, TimeUnit.NANOSECONDS);
    }

    @Override
    public void recordConnectionUsageMillis(long elapsedBorrowedMillis) {
        this.connectionUsage.update(elapsedBorrowedMillis);
    }

    @Override
    public void recordConnectionTimeout() {
        this.connectionTimeoutMeter.mark();
    }

    @Override
    public void recordConnectionCreatedMillis(long connectionCreatedMillis) {
        this.connectionCreation.update(connectionCreatedMillis);
    }

    public Timer getConnectionAcquisitionTimer() {
        return this.connectionObtainTimer;
    }

    public Histogram getConnectionDurationHistogram() {
        return this.connectionUsage;
    }

    public Histogram getConnectionCreationHistogram() {
        return this.connectionCreation;
    }
}

