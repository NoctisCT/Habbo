/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.codahale.metrics.MetricRegistry
 *  com.codahale.metrics.health.HealthCheckRegistry
 *  io.micrometer.core.instrument.MeterRegistry
 */
package com.zaxxer.hikari.pool;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariPoolMXBean;
import com.zaxxer.hikari.metrics.MetricsTrackerFactory;
import com.zaxxer.hikari.metrics.PoolStats;
import com.zaxxer.hikari.metrics.dropwizard.CodahaleHealthChecker;
import com.zaxxer.hikari.metrics.dropwizard.CodahaleMetricsTrackerFactory;
import com.zaxxer.hikari.metrics.micrometer.MicrometerMetricsTrackerFactory;
import com.zaxxer.hikari.pool.PoolBase;
import com.zaxxer.hikari.pool.PoolEntry;
import com.zaxxer.hikari.pool.ProxyConnection;
import com.zaxxer.hikari.pool.ProxyLeakTaskFactory;
import com.zaxxer.hikari.util.ClockSource;
import com.zaxxer.hikari.util.ConcurrentBag;
import com.zaxxer.hikari.util.SuspendResumeLock;
import com.zaxxer.hikari.util.UtilityElf;
import io.micrometer.core.instrument.MeterRegistry;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HikariPool
extends PoolBase
implements HikariPoolMXBean,
ConcurrentBag.IBagStateListener {
    private final Logger logger = LoggerFactory.getLogger(HikariPool.class);
    public static final int POOL_NORMAL = 0;
    public static final int POOL_SUSPENDED = 1;
    public static final int POOL_SHUTDOWN = 2;
    public volatile int poolState;
    private final long aliveBypassWindowMs = Long.getLong("com.zaxxer.hikari.aliveBypassWindowMs", TimeUnit.MILLISECONDS.toMillis(500L));
    private final long housekeepingPeriodMs = Long.getLong("com.zaxxer.hikari.housekeeping.periodMs", TimeUnit.SECONDS.toMillis(30L));
    private static final String EVICTED_CONNECTION_MESSAGE = "(connection was evicted)";
    private static final String DEAD_CONNECTION_MESSAGE = "(connection is dead)";
    private final PoolEntryCreator poolEntryCreator = new PoolEntryCreator(null);
    private final PoolEntryCreator postFillPoolEntryCreator = new PoolEntryCreator("After adding ");
    private final Collection<Runnable> addConnectionQueueReadOnlyView;
    private final ThreadPoolExecutor addConnectionExecutor;
    private final ThreadPoolExecutor closeConnectionExecutor;
    private final ConcurrentBag<PoolEntry> connectionBag = new ConcurrentBag(this);
    private final ProxyLeakTaskFactory leakTaskFactory;
    private final SuspendResumeLock suspendResumeLock;
    private final ScheduledExecutorService houseKeepingExecutorService;
    private ScheduledFuture<?> houseKeeperTask;

    public HikariPool(HikariConfig config) {
        super(config);
        this.suspendResumeLock = config.isAllowPoolSuspension() ? new SuspendResumeLock() : SuspendResumeLock.FAUX_LOCK;
        this.houseKeepingExecutorService = this.initializeHouseKeepingExecutorService();
        this.checkFailFast();
        if (config.getMetricsTrackerFactory() != null) {
            this.setMetricsTrackerFactory(config.getMetricsTrackerFactory());
        } else {
            this.setMetricRegistry(config.getMetricRegistry());
        }
        this.setHealthCheckRegistry(config.getHealthCheckRegistry());
        this.handleMBeans(this, true);
        ThreadFactory threadFactory = config.getThreadFactory();
        int maxPoolSize = config.getMaximumPoolSize();
        LinkedBlockingQueue<Runnable> addConnectionQueue = new LinkedBlockingQueue<Runnable>(maxPoolSize);
        this.addConnectionQueueReadOnlyView = Collections.unmodifiableCollection(addConnectionQueue);
        this.addConnectionExecutor = UtilityElf.createThreadPoolExecutor(addConnectionQueue, this.poolName + " connection adder", threadFactory, (RejectedExecutionHandler)new ThreadPoolExecutor.DiscardOldestPolicy());
        this.closeConnectionExecutor = UtilityElf.createThreadPoolExecutor(maxPoolSize, this.poolName + " connection closer", threadFactory, (RejectedExecutionHandler)new ThreadPoolExecutor.CallerRunsPolicy());
        this.leakTaskFactory = new ProxyLeakTaskFactory(config.getLeakDetectionThreshold(), this.houseKeepingExecutorService);
        this.houseKeeperTask = this.houseKeepingExecutorService.scheduleWithFixedDelay(new HouseKeeper(), 100L, this.housekeepingPeriodMs, TimeUnit.MILLISECONDS);
        if (Boolean.getBoolean("com.zaxxer.hikari.blockUntilFilled") && config.getInitializationFailTimeout() > 1L) {
            this.addConnectionExecutor.setCorePoolSize(Math.min(16, Runtime.getRuntime().availableProcessors()));
            this.addConnectionExecutor.setMaximumPoolSize(Math.min(16, Runtime.getRuntime().availableProcessors()));
            long startTime = ClockSource.currentTime();
            while (ClockSource.elapsedMillis(startTime) < config.getInitializationFailTimeout() && this.getTotalConnections() < config.getMinimumIdle()) {
                UtilityElf.quietlySleep(TimeUnit.MILLISECONDS.toMillis(100L));
            }
            this.addConnectionExecutor.setCorePoolSize(1);
            this.addConnectionExecutor.setMaximumPoolSize(1);
        }
    }

    public Connection getConnection() throws SQLException {
        return this.getConnection(this.connectionTimeout);
    }

    /*
     * Exception decompiling
     */
    public Connection getConnection(long hardTimeout) throws SQLException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [6[WHILELOOP]], but top level block is 2[TRYBLOCK]
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized void shutdown() throws InterruptedException {
        block8: {
            this.poolState = 2;
            if (this.addConnectionExecutor != null) break block8;
            this.logPoolState("After shutdown ");
            this.handleMBeans(this, false);
            this.metricsTracker.close();
            return;
        }
        try {
            this.logPoolState("Before shutdown ");
            if (this.houseKeeperTask != null) {
                this.houseKeeperTask.cancel(false);
                this.houseKeeperTask = null;
            }
            this.softEvictConnections();
            this.addConnectionExecutor.shutdown();
            this.addConnectionExecutor.awaitTermination(this.getLoginTimeout(), TimeUnit.SECONDS);
            this.destroyHouseKeepingExecutorService();
            this.connectionBag.close();
            ThreadPoolExecutor assassinExecutor = UtilityElf.createThreadPoolExecutor(this.config.getMaximumPoolSize(), this.poolName + " connection assassinator", this.config.getThreadFactory(), (RejectedExecutionHandler)new ThreadPoolExecutor.CallerRunsPolicy());
            try {
                long start = ClockSource.currentTime();
                do {
                    this.abortActiveConnections(assassinExecutor);
                    this.softEvictConnections();
                } while (this.getTotalConnections() > 0 && ClockSource.elapsedMillis(start) < TimeUnit.SECONDS.toMillis(10L));
            }
            finally {
                assassinExecutor.shutdown();
                assassinExecutor.awaitTermination(10L, TimeUnit.SECONDS);
            }
            this.shutdownNetworkTimeoutExecutor();
            this.closeConnectionExecutor.shutdown();
            this.closeConnectionExecutor.awaitTermination(10L, TimeUnit.SECONDS);
        }
        catch (Throwable throwable) {
            this.logPoolState("After shutdown ");
            this.handleMBeans(this, false);
            this.metricsTracker.close();
            throw throwable;
        }
        this.logPoolState("After shutdown ");
        this.handleMBeans(this, false);
        this.metricsTracker.close();
    }

    public void evictConnection(Connection connection) {
        ProxyConnection proxyConnection = (ProxyConnection)connection;
        proxyConnection.cancelLeakTask();
        try {
            this.softEvictConnection(proxyConnection.getPoolEntry(), "(connection evicted by user)", !connection.isClosed());
        }
        catch (SQLException sQLException) {
            // empty catch block
        }
    }

    public void setMetricRegistry(Object metricRegistry) {
        if (metricRegistry != null && UtilityElf.safeIsAssignableFrom(metricRegistry, "com.codahale.metrics.MetricRegistry")) {
            this.setMetricsTrackerFactory(new CodahaleMetricsTrackerFactory((MetricRegistry)metricRegistry));
        } else if (metricRegistry != null && UtilityElf.safeIsAssignableFrom(metricRegistry, "io.micrometer.core.instrument.MeterRegistry")) {
            this.setMetricsTrackerFactory(new MicrometerMetricsTrackerFactory((MeterRegistry)metricRegistry));
        } else {
            this.setMetricsTrackerFactory(null);
        }
    }

    public void setMetricsTrackerFactory(MetricsTrackerFactory metricsTrackerFactory) {
        this.metricsTracker = metricsTrackerFactory != null ? new PoolBase.MetricsTrackerDelegate(metricsTrackerFactory.create(this.config.getPoolName(), this.getPoolStats())) : new PoolBase.NopMetricsTrackerDelegate();
    }

    public void setHealthCheckRegistry(Object healthCheckRegistry) {
        if (healthCheckRegistry != null) {
            CodahaleHealthChecker.registerHealthChecks(this, this.config, (HealthCheckRegistry)healthCheckRegistry);
        }
    }

    @Override
    public void addBagItem(int waiting) {
        boolean shouldAdd;
        boolean bl = shouldAdd = waiting - this.addConnectionQueueReadOnlyView.size() >= 0;
        if (shouldAdd) {
            this.addConnectionExecutor.submit(this.poolEntryCreator);
        } else {
            this.logger.debug("{} - Add connection elided, waiting {}, queue {}", this.poolName, waiting, this.addConnectionQueueReadOnlyView.size());
        }
    }

    @Override
    public int getActiveConnections() {
        return this.connectionBag.getCount(1);
    }

    @Override
    public int getIdleConnections() {
        return this.connectionBag.getCount(0);
    }

    @Override
    public int getTotalConnections() {
        return this.connectionBag.size();
    }

    @Override
    public int getThreadsAwaitingConnection() {
        return this.connectionBag.getWaitingThreadCount();
    }

    @Override
    public void softEvictConnections() {
        this.connectionBag.values().forEach(poolEntry -> this.softEvictConnection((PoolEntry)poolEntry, "(connection evicted)", false));
    }

    @Override
    public synchronized void suspendPool() {
        if (this.suspendResumeLock == SuspendResumeLock.FAUX_LOCK) {
            throw new IllegalStateException(this.poolName + " - is not suspendable");
        }
        if (this.poolState != 1) {
            this.suspendResumeLock.suspend();
            this.poolState = 1;
        }
    }

    @Override
    public synchronized void resumePool() {
        if (this.poolState == 1) {
            this.poolState = 0;
            this.fillPool();
            this.suspendResumeLock.resume();
        }
    }

    void logPoolState(String ... prefix) {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("{} - {}stats (total={}, active={}, idle={}, waiting={})", this.poolName, prefix.length > 0 ? prefix[0] : "", this.getTotalConnections(), this.getActiveConnections(), this.getIdleConnections(), this.getThreadsAwaitingConnection());
        }
    }

    @Override
    void recycle(PoolEntry poolEntry) {
        this.metricsTracker.recordConnectionUsage(poolEntry);
        this.connectionBag.requite(poolEntry);
    }

    void closeConnection(PoolEntry poolEntry, String closureReason) {
        if (this.connectionBag.remove(poolEntry)) {
            Connection connection = poolEntry.close();
            this.closeConnectionExecutor.execute(() -> {
                this.quietlyCloseConnection(connection, closureReason);
                if (this.poolState == 0) {
                    this.fillPool();
                }
            });
        }
    }

    int[] getPoolStateCounts() {
        return this.connectionBag.getStateCounts();
    }

    private PoolEntry createPoolEntry() {
        block5: {
            try {
                PoolEntry poolEntry = this.newPoolEntry();
                long maxLifetime = this.config.getMaxLifetime();
                if (maxLifetime > 0L) {
                    long variance = maxLifetime > 10000L ? ThreadLocalRandom.current().nextLong(maxLifetime / 40L) : 0L;
                    long lifetime = maxLifetime - variance;
                    poolEntry.setFutureEol(this.houseKeepingExecutorService.schedule(() -> {
                        if (this.softEvictConnection(poolEntry, "(connection has passed maxLifetime)", false)) {
                            this.addBagItem(this.connectionBag.getWaitingThreadCount());
                        }
                    }, lifetime, TimeUnit.MILLISECONDS));
                }
                return poolEntry;
            }
            catch (PoolBase.ConnectionSetupException e) {
                if (this.poolState == 0) {
                    this.logger.error("{} - Error thrown while acquiring connection from data source", (Object)this.poolName, (Object)e.getCause());
                    this.lastConnectionFailure.set(e);
                }
            }
            catch (Exception e) {
                if (this.poolState != 0) break block5;
                this.logger.debug("{} - Cannot acquire connection from data source", (Object)this.poolName, (Object)e);
            }
        }
        return null;
    }

    private synchronized void fillPool() {
        int connectionsToAdd = Math.min(this.config.getMaximumPoolSize() - this.getTotalConnections(), this.config.getMinimumIdle() - this.getIdleConnections()) - this.addConnectionQueueReadOnlyView.size();
        if (connectionsToAdd <= 0) {
            this.logger.debug("{} - Fill pool skipped, pool is at sufficient level.", (Object)this.poolName);
        }
        for (int i = 0; i < connectionsToAdd; ++i) {
            this.addConnectionExecutor.submit(i < connectionsToAdd - 1 ? this.poolEntryCreator : this.postFillPoolEntryCreator);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void abortActiveConnections(ExecutorService assassinExecutor) {
        for (PoolEntry poolEntry : this.connectionBag.values(1)) {
            Connection connection = poolEntry.close();
            try {
                connection.abort(assassinExecutor);
            }
            catch (Throwable e) {
                this.quietlyCloseConnection(connection, "(connection aborted during shutdown)");
            }
            finally {
                this.connectionBag.remove(poolEntry);
            }
        }
    }

    private void checkFailFast() {
        long initializationTimeout = this.config.getInitializationFailTimeout();
        if (initializationTimeout < 0L) {
            return;
        }
        long startTime = ClockSource.currentTime();
        do {
            PoolEntry poolEntry;
            if ((poolEntry = this.createPoolEntry()) != null) {
                if (this.config.getMinimumIdle() > 0) {
                    this.connectionBag.add(poolEntry);
                    this.logger.debug("{} - Added connection {}", (Object)this.poolName, (Object)poolEntry.connection);
                } else {
                    this.quietlyCloseConnection(poolEntry.close(), "(initialization check complete and minimumIdle is zero)");
                }
                return;
            }
            if (this.getLastConnectionFailure() instanceof PoolBase.ConnectionSetupException) {
                this.throwPoolInitializationException(this.getLastConnectionFailure().getCause());
            }
            UtilityElf.quietlySleep(TimeUnit.SECONDS.toMillis(1L));
        } while (ClockSource.elapsedMillis(startTime) < initializationTimeout);
        if (initializationTimeout > 0L) {
            this.throwPoolInitializationException(this.getLastConnectionFailure());
        }
    }

    private void throwPoolInitializationException(Throwable t) {
        this.logger.error("{} - Exception during pool initialization.", (Object)this.poolName, (Object)t);
        this.destroyHouseKeepingExecutorService();
        throw new PoolInitializationException(t);
    }

    private boolean softEvictConnection(PoolEntry poolEntry, String reason, boolean owner) {
        poolEntry.markEvicted();
        if (owner || this.connectionBag.reserve(poolEntry)) {
            this.closeConnection(poolEntry, reason);
            return true;
        }
        return false;
    }

    private ScheduledExecutorService initializeHouseKeepingExecutorService() {
        if (this.config.getScheduledExecutor() == null) {
            ThreadFactory threadFactory = Optional.ofNullable(this.config.getThreadFactory()).orElseGet(() -> new UtilityElf.DefaultThreadFactory(this.poolName + " housekeeper", true));
            ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1, threadFactory, new ThreadPoolExecutor.DiscardPolicy());
            executor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
            executor.setRemoveOnCancelPolicy(true);
            return executor;
        }
        return this.config.getScheduledExecutor();
    }

    private void destroyHouseKeepingExecutorService() {
        if (this.config.getScheduledExecutor() == null) {
            this.houseKeepingExecutorService.shutdownNow();
        }
    }

    private PoolStats getPoolStats() {
        return new PoolStats(TimeUnit.SECONDS.toMillis(1L)){

            @Override
            protected void update() {
                this.pendingThreads = HikariPool.this.getThreadsAwaitingConnection();
                this.idleConnections = HikariPool.this.getIdleConnections();
                this.totalConnections = HikariPool.this.getTotalConnections();
                this.activeConnections = HikariPool.this.getActiveConnections();
                this.maxConnections = HikariPool.this.config.getMaximumPoolSize();
                this.minConnections = HikariPool.this.config.getMinimumIdle();
            }
        };
    }

    private SQLException createTimeoutException(long startTime) {
        this.logPoolState("Timeout failure ");
        this.metricsTracker.recordConnectionTimeout();
        String sqlState = null;
        Exception originalException = this.getLastConnectionFailure();
        if (originalException instanceof SQLException) {
            sqlState = ((SQLException)originalException).getSQLState();
        }
        SQLTransientConnectionException connectionException = new SQLTransientConnectionException(this.poolName + " - Connection is not available, request timed out after " + ClockSource.elapsedMillis(startTime) + "ms.", sqlState, originalException);
        if (originalException instanceof SQLException) {
            connectionException.setNextException((SQLException)originalException);
        }
        return connectionException;
    }

    public static class PoolInitializationException
    extends RuntimeException {
        private static final long serialVersionUID = 929872118275916520L;

        public PoolInitializationException(Throwable t) {
            super("Failed to initialize pool: " + t.getMessage(), t);
        }
    }

    private final class HouseKeeper
    implements Runnable {
        private volatile long previous;

        private HouseKeeper() {
            this.previous = ClockSource.plusMillis(ClockSource.currentTime(), -HikariPool.this.housekeepingPeriodMs);
        }

        @Override
        public void run() {
            try {
                HikariPool.this.connectionTimeout = HikariPool.this.config.getConnectionTimeout();
                HikariPool.this.validationTimeout = HikariPool.this.config.getValidationTimeout();
                HikariPool.this.leakTaskFactory.updateLeakDetectionThreshold(HikariPool.this.config.getLeakDetectionThreshold());
                HikariPool.this.catalog = HikariPool.this.config.getCatalog() != null && !HikariPool.this.config.getCatalog().equals(HikariPool.this.catalog) ? HikariPool.this.config.getCatalog() : HikariPool.this.catalog;
                long idleTimeout = HikariPool.this.config.getIdleTimeout();
                long now = ClockSource.currentTime();
                if (ClockSource.plusMillis(now, 128L) < ClockSource.plusMillis(this.previous, HikariPool.this.housekeepingPeriodMs)) {
                    HikariPool.this.logger.warn("{} - Retrograde clock change detected (housekeeper delta={}), soft-evicting connections from pool.", (Object)HikariPool.this.poolName, (Object)ClockSource.elapsedDisplayString(this.previous, now));
                    this.previous = now;
                    HikariPool.this.softEvictConnections();
                    return;
                }
                if (now > ClockSource.plusMillis(this.previous, 3L * HikariPool.this.housekeepingPeriodMs / 2L)) {
                    HikariPool.this.logger.warn("{} - Thread starvation or clock leap detected (housekeeper delta={}).", (Object)HikariPool.this.poolName, (Object)ClockSource.elapsedDisplayString(this.previous, now));
                }
                this.previous = now;
                String afterPrefix = "Pool ";
                if (idleTimeout > 0L && HikariPool.this.config.getMinimumIdle() < HikariPool.this.config.getMaximumPoolSize()) {
                    HikariPool.this.logPoolState("Before cleanup ");
                    afterPrefix = "After cleanup  ";
                    List notInUse = HikariPool.this.connectionBag.values(0);
                    int toRemove = notInUse.size() - HikariPool.this.config.getMinimumIdle();
                    for (PoolEntry entry : notInUse) {
                        if (toRemove <= 0 || ClockSource.elapsedMillis(entry.lastAccessed, now) <= idleTimeout || !HikariPool.this.connectionBag.reserve(entry)) continue;
                        HikariPool.this.closeConnection(entry, "(connection has passed idleTimeout)");
                        --toRemove;
                    }
                }
                HikariPool.this.logPoolState(afterPrefix);
                HikariPool.this.fillPool();
            }
            catch (Exception e) {
                HikariPool.this.logger.error("Unexpected exception in housekeeping task", e);
            }
        }
    }

    private final class PoolEntryCreator
    implements Callable<Boolean> {
        private final String loggingPrefix;

        PoolEntryCreator(String loggingPrefix) {
            this.loggingPrefix = loggingPrefix;
        }

        @Override
        public Boolean call() {
            long sleepBackoff = 250L;
            while (HikariPool.this.poolState == 0 && this.shouldCreateAnotherConnection()) {
                PoolEntry poolEntry = HikariPool.this.createPoolEntry();
                if (poolEntry != null) {
                    HikariPool.this.connectionBag.add(poolEntry);
                    HikariPool.this.logger.debug("{} - Added connection {}", (Object)HikariPool.this.poolName, (Object)poolEntry.connection);
                    if (this.loggingPrefix != null) {
                        HikariPool.this.logPoolState(this.loggingPrefix);
                    }
                    return Boolean.TRUE;
                }
                if (this.loggingPrefix != null) {
                    HikariPool.this.logger.debug("{} - Connection add failed, sleeping with backoff: {}ms", (Object)HikariPool.this.poolName, (Object)sleepBackoff);
                }
                UtilityElf.quietlySleep(sleepBackoff);
                sleepBackoff = Math.min(TimeUnit.SECONDS.toMillis(10L), Math.min(HikariPool.this.connectionTimeout, (long)((double)sleepBackoff * 1.5)));
            }
            return Boolean.FALSE;
        }

        private synchronized boolean shouldCreateAnotherConnection() {
            return HikariPool.this.getTotalConnections() < HikariPool.this.config.getMaximumPoolSize() && (HikariPool.this.connectionBag.getWaitingThreadCount() > 0 || HikariPool.this.getIdleConnections() < HikariPool.this.config.getMinimumIdle());
        }
    }
}

