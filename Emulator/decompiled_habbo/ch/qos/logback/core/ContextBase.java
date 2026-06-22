/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core;

import ch.qos.logback.core.BasicStatusManager;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.LifeCycleManager;
import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.spi.LogbackLock;
import ch.qos.logback.core.status.StatusManager;
import ch.qos.logback.core.util.ContextUtil;
import ch.qos.logback.core.util.ExecutorServiceUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

public class ContextBase
implements Context,
LifeCycle {
    private long birthTime = System.currentTimeMillis();
    private String name;
    private StatusManager sm = new BasicStatusManager();
    Map<String, String> propertyMap = new HashMap<String, String>();
    Map<String, Object> objectMap = new HashMap<String, Object>();
    LogbackLock configurationLock = new LogbackLock();
    private ScheduledExecutorService scheduledExecutorService;
    protected List<ScheduledFuture<?>> scheduledFutures = new ArrayList(1);
    private LifeCycleManager lifeCycleManager;
    private boolean started;

    public ContextBase() {
        this.initCollisionMaps();
    }

    @Override
    public StatusManager getStatusManager() {
        return this.sm;
    }

    public void setStatusManager(StatusManager statusManager) {
        if (statusManager == null) {
            throw new IllegalArgumentException("null StatusManager not allowed");
        }
        this.sm = statusManager;
    }

    @Override
    public Map<String, String> getCopyOfPropertyMap() {
        return new HashMap<String, String>(this.propertyMap);
    }

    @Override
    public void putProperty(String key, String val) {
        if ("HOSTNAME".equalsIgnoreCase(key)) {
            this.putHostnameProperty(val);
        } else {
            this.propertyMap.put(key, val);
        }
    }

    protected void initCollisionMaps() {
        this.putObject("FA_FILENAME_COLLISION_MAP", new HashMap());
        this.putObject("RFA_FILENAME_PATTERN_COLLISION_MAP", new HashMap());
    }

    @Override
    public String getProperty(String key) {
        if ("CONTEXT_NAME".equals(key)) {
            return this.getName();
        }
        if ("HOSTNAME".equalsIgnoreCase(key)) {
            return this.lazyGetHostname();
        }
        return this.propertyMap.get(key);
    }

    private String lazyGetHostname() {
        String hostname = this.propertyMap.get("HOSTNAME");
        if (hostname == null) {
            hostname = new ContextUtil(this).safelyGetLocalHostName();
            this.putHostnameProperty(hostname);
        }
        return hostname;
    }

    private void putHostnameProperty(String hostname) {
        String existingHostname = this.propertyMap.get("HOSTNAME");
        if (existingHostname == null) {
            this.propertyMap.put("HOSTNAME", hostname);
        }
    }

    @Override
    public Object getObject(String key) {
        return this.objectMap.get(key);
    }

    @Override
    public void putObject(String key, Object value) {
        this.objectMap.put(key, value);
    }

    public void removeObject(String key) {
        this.objectMap.remove(key);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void start() {
        this.started = true;
    }

    @Override
    public void stop() {
        this.stopExecutorService();
        this.started = false;
    }

    @Override
    public boolean isStarted() {
        return this.started;
    }

    public void reset() {
        this.removeShutdownHook();
        this.getLifeCycleManager().reset();
        this.propertyMap.clear();
        this.objectMap.clear();
    }

    @Override
    public void setName(String name) throws IllegalStateException {
        if (name != null && name.equals(this.name)) {
            return;
        }
        if (this.name != null && !"default".equals(this.name)) {
            throw new IllegalStateException("Context has been already given a name");
        }
        this.name = name;
    }

    @Override
    public long getBirthTime() {
        return this.birthTime;
    }

    @Override
    public Object getConfigurationLock() {
        return this.configurationLock;
    }

    @Override
    public synchronized ExecutorService getExecutorService() {
        return this.getScheduledExecutorService();
    }

    @Override
    public synchronized ScheduledExecutorService getScheduledExecutorService() {
        if (this.scheduledExecutorService == null) {
            this.scheduledExecutorService = ExecutorServiceUtil.newScheduledExecutorService();
        }
        return this.scheduledExecutorService;
    }

    private synchronized void stopExecutorService() {
        if (this.scheduledExecutorService != null) {
            ExecutorServiceUtil.shutdown(this.scheduledExecutorService);
            this.scheduledExecutorService = null;
        }
    }

    private void removeShutdownHook() {
        Thread hook = (Thread)this.getObject("SHUTDOWN_HOOK");
        if (hook != null) {
            this.removeObject("SHUTDOWN_HOOK");
            try {
                Runtime.getRuntime().removeShutdownHook(hook);
            }
            catch (IllegalStateException illegalStateException) {
                // empty catch block
            }
        }
    }

    @Override
    public void register(LifeCycle component) {
        this.getLifeCycleManager().register(component);
    }

    synchronized LifeCycleManager getLifeCycleManager() {
        if (this.lifeCycleManager == null) {
            this.lifeCycleManager = new LifeCycleManager();
        }
        return this.lifeCycleManager;
    }

    public String toString() {
        return this.name;
    }

    @Override
    public void addScheduledFuture(ScheduledFuture<?> scheduledFuture) {
        this.scheduledFutures.add(scheduledFuture);
    }

    public List<ScheduledFuture<?>> getScheduledFutures() {
        return new ArrayList(this.scheduledFutures);
    }
}

