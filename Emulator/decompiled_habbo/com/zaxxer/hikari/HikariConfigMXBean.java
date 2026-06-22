/*
 * Decompiled with CFR 0.152.
 */
package com.zaxxer.hikari;

public interface HikariConfigMXBean {
    public long getConnectionTimeout();

    public void setConnectionTimeout(long var1);

    public long getValidationTimeout();

    public void setValidationTimeout(long var1);

    public long getIdleTimeout();

    public void setIdleTimeout(long var1);

    public long getLeakDetectionThreshold();

    public void setLeakDetectionThreshold(long var1);

    public long getMaxLifetime();

    public void setMaxLifetime(long var1);

    public int getMinimumIdle();

    public void setMinimumIdle(int var1);

    public int getMaximumPoolSize();

    public void setMaximumPoolSize(int var1);

    public void setPassword(String var1);

    public void setUsername(String var1);

    public String getPoolName();

    public String getCatalog();

    public void setCatalog(String var1);
}

