/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.prometheus.client.CollectorRegistry
 *  io.prometheus.client.Counter
 *  io.prometheus.client.Counter$Builder
 *  io.prometheus.client.Counter$Child
 *  io.prometheus.client.Histogram
 *  io.prometheus.client.Histogram$Builder
 *  io.prometheus.client.Histogram$Child
 */
package com.zaxxer.hikari.metrics.prometheus;

import com.zaxxer.hikari.metrics.IMetricsTracker;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;

class PrometheusHistogramMetricsTracker
implements IMetricsTracker {
    private static final Counter CONNECTION_TIMEOUT_COUNTER = ((Counter.Builder)((Counter.Builder)((Counter.Builder)Counter.build().name("hikaricp_connection_timeout_total")).labelNames(new String[]{"pool"})).help("Connection timeout total count")).create();
    private static final Histogram ELAPSED_ACQUIRED_HISTOGRAM = PrometheusHistogramMetricsTracker.registerHistogram("hikaricp_connection_acquired_nanos", "Connection acquired time (ns)", 1000.0);
    private static final Histogram ELAPSED_BORROWED_HISTOGRAM = PrometheusHistogramMetricsTracker.registerHistogram("hikaricp_connection_usage_millis", "Connection usage (ms)", 1.0);
    private static final Histogram ELAPSED_CREATION_HISTOGRAM = PrometheusHistogramMetricsTracker.registerHistogram("hikaricp_connection_creation_millis", "Connection creation (ms)", 1.0);
    private final Counter.Child connectionTimeoutCounterChild;
    private final Histogram.Child elapsedAcquiredHistogramChild;
    private final Histogram.Child elapsedBorrowedHistogramChild;
    private final Histogram.Child elapsedCreationHistogramChild;

    private static Histogram registerHistogram(String name, String help, double bucketStart) {
        return ((Histogram.Builder)((Histogram.Builder)((Histogram.Builder)Histogram.build().name(name)).labelNames(new String[]{"pool"})).help(help)).exponentialBuckets(bucketStart, 2.0, 11).create();
    }

    PrometheusHistogramMetricsTracker(String poolName, CollectorRegistry collectorRegistry) {
        this.registerMetrics(collectorRegistry);
        this.connectionTimeoutCounterChild = (Counter.Child)CONNECTION_TIMEOUT_COUNTER.labels(new String[]{poolName});
        this.elapsedAcquiredHistogramChild = (Histogram.Child)ELAPSED_ACQUIRED_HISTOGRAM.labels(new String[]{poolName});
        this.elapsedBorrowedHistogramChild = (Histogram.Child)ELAPSED_BORROWED_HISTOGRAM.labels(new String[]{poolName});
        this.elapsedCreationHistogramChild = (Histogram.Child)ELAPSED_CREATION_HISTOGRAM.labels(new String[]{poolName});
    }

    private void registerMetrics(CollectorRegistry collectorRegistry) {
        CONNECTION_TIMEOUT_COUNTER.register(collectorRegistry);
        ELAPSED_ACQUIRED_HISTOGRAM.register(collectorRegistry);
        ELAPSED_BORROWED_HISTOGRAM.register(collectorRegistry);
        ELAPSED_CREATION_HISTOGRAM.register(collectorRegistry);
    }

    @Override
    public void recordConnectionAcquiredNanos(long elapsedAcquiredNanos) {
        this.elapsedAcquiredHistogramChild.observe((double)elapsedAcquiredNanos);
    }

    @Override
    public void recordConnectionUsageMillis(long elapsedBorrowedMillis) {
        this.elapsedBorrowedHistogramChild.observe((double)elapsedBorrowedMillis);
    }

    @Override
    public void recordConnectionCreatedMillis(long connectionCreatedMillis) {
        this.elapsedCreationHistogramChild.observe((double)connectionCreatedMillis);
    }

    @Override
    public void recordConnectionTimeout() {
        this.connectionTimeoutCounterChild.inc();
    }
}

