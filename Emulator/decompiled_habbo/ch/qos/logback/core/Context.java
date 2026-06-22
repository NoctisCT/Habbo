/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core;

import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.spi.PropertyContainer;
import ch.qos.logback.core.status.StatusManager;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

public interface Context
extends PropertyContainer {
    public StatusManager getStatusManager();

    public Object getObject(String var1);

    public void putObject(String var1, Object var2);

    @Override
    public String getProperty(String var1);

    public void putProperty(String var1, String var2);

    @Override
    public Map<String, String> getCopyOfPropertyMap();

    public String getName();

    public void setName(String var1);

    public long getBirthTime();

    public Object getConfigurationLock();

    public ScheduledExecutorService getScheduledExecutorService();

    public ExecutorService getExecutorService();

    public void register(LifeCycle var1);

    public void addScheduledFuture(ScheduledFuture<?> var1);
}

