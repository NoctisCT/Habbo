/*
 * Decompiled with CFR 0.152.
 */
package com.zaxxer.hikari.metrics;

public interface IMetricsTracker
extends AutoCloseable {
    default public void recordConnectionCreatedMillis(long connectionCreatedMillis) {
    }

    default public void recordConnectionAcquiredNanos(long elapsedAcquiredNanos) {
    }

    default public void recordConnectionUsageMillis(long elapsedBorrowedMillis) {
    }

    default public void recordConnectionTimeout() {
    }

    @Override
    default public void close() {
    }
}

