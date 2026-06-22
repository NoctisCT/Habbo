/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.LoggerComparator;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.classic.spi.LoggerContextVO;
import ch.qos.logback.classic.spi.TurboFilterList;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.classic.util.LoggerNameUtil;
import ch.qos.logback.core.ContextBase;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.status.StatusListener;
import ch.qos.logback.core.status.StatusManager;
import ch.qos.logback.core.status.WarnStatus;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import org.slf4j.ILoggerFactory;
import org.slf4j.Marker;

public class LoggerContext
extends ContextBase
implements ILoggerFactory,
LifeCycle {
    public static final boolean DEFAULT_PACKAGING_DATA = false;
    final Logger root;
    private int size;
    private int noAppenderWarning = 0;
    private final List<LoggerContextListener> loggerContextListenerList = new ArrayList<LoggerContextListener>();
    private Map<String, Logger> loggerCache;
    private LoggerContextVO loggerContextRemoteView;
    private final TurboFilterList turboFilterList = new TurboFilterList();
    private boolean packagingDataEnabled = false;
    private int maxCallerDataDepth = 8;
    int resetCount = 0;
    private List<String> frameworkPackages;

    public LoggerContext() {
        this.loggerCache = new ConcurrentHashMap<String, Logger>();
        this.loggerContextRemoteView = new LoggerContextVO(this);
        this.root = new Logger("ROOT", null, this);
        this.root.setLevel(Level.DEBUG);
        this.loggerCache.put("ROOT", this.root);
        this.initEvaluatorMap();
        this.size = 1;
        this.frameworkPackages = new ArrayList<String>();
    }

    void initEvaluatorMap() {
        this.putObject("EVALUATOR_MAP", new HashMap());
    }

    private void updateLoggerContextVO() {
        this.loggerContextRemoteView = new LoggerContextVO(this);
    }

    @Override
    public void putProperty(String key, String val) {
        super.putProperty(key, val);
        this.updateLoggerContextVO();
    }

    @Override
    public void setName(String name) {
        super.setName(name);
        this.updateLoggerContextVO();
    }

    public final Logger getLogger(Class<?> clazz) {
        return this.getLogger(clazz.getName());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final Logger getLogger(String name) {
        int h;
        if (name == null) {
            throw new IllegalArgumentException("name argument cannot be null");
        }
        if ("ROOT".equalsIgnoreCase(name)) {
            return this.root;
        }
        int i = 0;
        Logger logger = this.root;
        Logger childLogger = this.loggerCache.get(name);
        if (childLogger != null) {
            return childLogger;
        }
        do {
            String childName = (h = LoggerNameUtil.getSeparatorIndexOf(name, i)) == -1 ? name : name.substring(0, h);
            i = h + 1;
            Logger logger2 = logger;
            synchronized (logger2) {
                childLogger = logger.getChildByName(childName);
                if (childLogger == null) {
                    childLogger = logger.createChildByName(childName);
                    this.loggerCache.put(childName, childLogger);
                    this.incSize();
                }
            }
            logger = childLogger;
        } while (h != -1);
        return childLogger;
    }

    private void incSize() {
        ++this.size;
    }

    int size() {
        return this.size;
    }

    public Logger exists(String name) {
        return this.loggerCache.get(name);
    }

    final void noAppenderDefinedWarning(Logger logger) {
        if (this.noAppenderWarning++ == 0) {
            this.getStatusManager().add(new WarnStatus("No appenders present in context [" + this.getName() + "] for logger [" + logger.getName() + "].", logger));
        }
    }

    public List<Logger> getLoggerList() {
        Collection<Logger> collection = this.loggerCache.values();
        ArrayList<Logger> loggerList = new ArrayList<Logger>(collection);
        Collections.sort(loggerList, new LoggerComparator());
        return loggerList;
    }

    public LoggerContextVO getLoggerContextRemoteView() {
        return this.loggerContextRemoteView;
    }

    public void setPackagingDataEnabled(boolean packagingDataEnabled) {
        this.packagingDataEnabled = packagingDataEnabled;
    }

    public boolean isPackagingDataEnabled() {
        return this.packagingDataEnabled;
    }

    @Override
    public void reset() {
        ++this.resetCount;
        super.reset();
        this.initEvaluatorMap();
        this.initCollisionMaps();
        this.root.recursiveReset();
        this.resetTurboFilterList();
        this.cancelScheduledTasks();
        this.fireOnReset();
        this.resetListenersExceptResetResistant();
        this.resetStatusListeners();
    }

    private void cancelScheduledTasks() {
        for (ScheduledFuture sf : this.scheduledFutures) {
            sf.cancel(false);
        }
        this.scheduledFutures.clear();
    }

    private void resetStatusListeners() {
        StatusManager sm = this.getStatusManager();
        for (StatusListener sl : sm.getCopyOfStatusListenerList()) {
            sm.remove(sl);
        }
    }

    public TurboFilterList getTurboFilterList() {
        return this.turboFilterList;
    }

    public void addTurboFilter(TurboFilter newFilter) {
        this.turboFilterList.add(newFilter);
    }

    public void resetTurboFilterList() {
        for (TurboFilter tf : this.turboFilterList) {
            tf.stop();
        }
        this.turboFilterList.clear();
    }

    final FilterReply getTurboFilterChainDecision_0_3OrMore(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
        if (this.turboFilterList.size() == 0) {
            return FilterReply.NEUTRAL;
        }
        return this.turboFilterList.getTurboFilterChainDecision(marker, logger, level, format, params, t);
    }

    final FilterReply getTurboFilterChainDecision_1(Marker marker, Logger logger, Level level, String format, Object param, Throwable t) {
        if (this.turboFilterList.size() == 0) {
            return FilterReply.NEUTRAL;
        }
        return this.turboFilterList.getTurboFilterChainDecision(marker, logger, level, format, new Object[]{param}, t);
    }

    final FilterReply getTurboFilterChainDecision_2(Marker marker, Logger logger, Level level, String format, Object param1, Object param2, Throwable t) {
        if (this.turboFilterList.size() == 0) {
            return FilterReply.NEUTRAL;
        }
        return this.turboFilterList.getTurboFilterChainDecision(marker, logger, level, format, new Object[]{param1, param2}, t);
    }

    public void addListener(LoggerContextListener listener) {
        this.loggerContextListenerList.add(listener);
    }

    public void removeListener(LoggerContextListener listener) {
        this.loggerContextListenerList.remove(listener);
    }

    private void resetListenersExceptResetResistant() {
        ArrayList<LoggerContextListener> toRetain = new ArrayList<LoggerContextListener>();
        for (LoggerContextListener lcl : this.loggerContextListenerList) {
            if (!lcl.isResetResistant()) continue;
            toRetain.add(lcl);
        }
        this.loggerContextListenerList.retainAll(toRetain);
    }

    private void resetAllListeners() {
        this.loggerContextListenerList.clear();
    }

    public List<LoggerContextListener> getCopyOfListenerList() {
        return new ArrayList<LoggerContextListener>(this.loggerContextListenerList);
    }

    void fireOnLevelChange(Logger logger, Level level) {
        for (LoggerContextListener listener : this.loggerContextListenerList) {
            listener.onLevelChange(logger, level);
        }
    }

    private void fireOnReset() {
        for (LoggerContextListener listener : this.loggerContextListenerList) {
            listener.onReset(this);
        }
    }

    private void fireOnStart() {
        for (LoggerContextListener listener : this.loggerContextListenerList) {
            listener.onStart(this);
        }
    }

    private void fireOnStop() {
        for (LoggerContextListener listener : this.loggerContextListenerList) {
            listener.onStop(this);
        }
    }

    @Override
    public void start() {
        super.start();
        this.fireOnStart();
    }

    @Override
    public void stop() {
        this.reset();
        this.fireOnStop();
        this.resetAllListeners();
        super.stop();
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "[" + this.getName() + "]";
    }

    public int getMaxCallerDataDepth() {
        return this.maxCallerDataDepth;
    }

    public void setMaxCallerDataDepth(int maxCallerDataDepth) {
        this.maxCallerDataDepth = maxCallerDataDepth;
    }

    public List<String> getFrameworkPackages() {
        return this.frameworkPackages;
    }
}

