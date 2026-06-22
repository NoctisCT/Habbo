/*
 * Decompiled with CFR 0.152.
 */
package com.zaxxer.hikari.metrics;

import com.zaxxer.hikari.metrics.IMetricsTracker;
import com.zaxxer.hikari.metrics.PoolStats;

public interface MetricsTrackerFactory {
    public IMetricsTracker create(String var1, PoolStats var2);
}

