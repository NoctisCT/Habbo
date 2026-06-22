/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.spi;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.CallerData;
import ch.qos.logback.classic.spi.EventArgUtil;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.LoggerContextVO;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.classic.util.LogbackMDCAdapter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Map;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.spi.MDCAdapter;

public class LoggingEvent
implements ILoggingEvent {
    transient String fqnOfLoggerClass;
    private String threadName;
    private String loggerName;
    private LoggerContext loggerContext;
    private LoggerContextVO loggerContextVO;
    private transient Level level;
    private String message;
    transient String formattedMessage;
    private transient Object[] argumentArray;
    private ThrowableProxy throwableProxy;
    private StackTraceElement[] callerDataArray;
    private Marker marker;
    private Map<String, String> mdcPropertyMap;
    private long timeStamp;

    public LoggingEvent() {
    }

    public LoggingEvent(String fqcn, Logger logger, Level level, String message, Throwable throwable, Object[] argArray) {
        this.fqnOfLoggerClass = fqcn;
        this.loggerName = logger.getName();
        this.loggerContext = logger.getLoggerContext();
        this.loggerContextVO = this.loggerContext.getLoggerContextRemoteView();
        this.level = level;
        this.message = message;
        this.argumentArray = argArray;
        if (throwable == null) {
            throwable = this.extractThrowableAnRearrangeArguments(argArray);
        }
        if (throwable != null) {
            this.throwableProxy = new ThrowableProxy(throwable);
            LoggerContext lc = logger.getLoggerContext();
            if (lc.isPackagingDataEnabled()) {
                this.throwableProxy.calculatePackagingData();
            }
        }
        this.timeStamp = System.currentTimeMillis();
    }

    private Throwable extractThrowableAnRearrangeArguments(Object[] argArray) {
        Throwable extractedThrowable = EventArgUtil.extractThrowable(argArray);
        if (EventArgUtil.successfulExtraction(extractedThrowable)) {
            this.argumentArray = EventArgUtil.trimmedCopy(argArray);
        }
        return extractedThrowable;
    }

    public void setArgumentArray(Object[] argArray) {
        if (this.argumentArray != null) {
            throw new IllegalStateException("argArray has been already set");
        }
        this.argumentArray = argArray;
    }

    @Override
    public Object[] getArgumentArray() {
        return this.argumentArray;
    }

    @Override
    public Level getLevel() {
        return this.level;
    }

    @Override
    public String getLoggerName() {
        return this.loggerName;
    }

    public void setLoggerName(String loggerName) {
        this.loggerName = loggerName;
    }

    @Override
    public String getThreadName() {
        if (this.threadName == null) {
            this.threadName = Thread.currentThread().getName();
        }
        return this.threadName;
    }

    public void setThreadName(String threadName) throws IllegalStateException {
        if (this.threadName != null) {
            throw new IllegalStateException("threadName has been already set");
        }
        this.threadName = threadName;
    }

    @Override
    public IThrowableProxy getThrowableProxy() {
        return this.throwableProxy;
    }

    public void setThrowableProxy(ThrowableProxy tp) {
        if (this.throwableProxy != null) {
            throw new IllegalStateException("ThrowableProxy has been already set.");
        }
        this.throwableProxy = tp;
    }

    @Override
    public void prepareForDeferredProcessing() {
        this.getFormattedMessage();
        this.getThreadName();
        this.getMDCPropertyMap();
    }

    @Override
    public LoggerContextVO getLoggerContextVO() {
        return this.loggerContextVO;
    }

    public void setLoggerContextRemoteView(LoggerContextVO loggerContextVO) {
        this.loggerContextVO = loggerContextVO;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        if (this.message != null) {
            throw new IllegalStateException("The message for this event has been set already.");
        }
        this.message = message;
    }

    @Override
    public long getTimeStamp() {
        return this.timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setLevel(Level level) {
        if (this.level != null) {
            throw new IllegalStateException("The level has been already set for this event.");
        }
        this.level = level;
    }

    @Override
    public StackTraceElement[] getCallerData() {
        if (this.callerDataArray == null) {
            this.callerDataArray = CallerData.extract(new Throwable(), this.fqnOfLoggerClass, this.loggerContext.getMaxCallerDataDepth(), this.loggerContext.getFrameworkPackages());
        }
        return this.callerDataArray;
    }

    @Override
    public boolean hasCallerData() {
        return this.callerDataArray != null;
    }

    public void setCallerData(StackTraceElement[] callerDataArray) {
        this.callerDataArray = callerDataArray;
    }

    @Override
    public Marker getMarker() {
        return this.marker;
    }

    public void setMarker(Marker marker) {
        if (this.marker != null) {
            throw new IllegalStateException("The marker has been already set for this event.");
        }
        this.marker = marker;
    }

    public long getContextBirthTime() {
        return this.loggerContextVO.getBirthTime();
    }

    @Override
    public String getFormattedMessage() {
        if (this.formattedMessage != null) {
            return this.formattedMessage;
        }
        this.formattedMessage = this.argumentArray != null ? MessageFormatter.arrayFormat(this.message, this.argumentArray).getMessage() : this.message;
        return this.formattedMessage;
    }

    @Override
    public Map<String, String> getMDCPropertyMap() {
        if (this.mdcPropertyMap == null) {
            MDCAdapter mdc = MDC.getMDCAdapter();
            this.mdcPropertyMap = mdc instanceof LogbackMDCAdapter ? ((LogbackMDCAdapter)mdc).getPropertyMap() : mdc.getCopyOfContextMap();
        }
        if (this.mdcPropertyMap == null) {
            this.mdcPropertyMap = Collections.emptyMap();
        }
        return this.mdcPropertyMap;
    }

    public void setMDCPropertyMap(Map<String, String> map) {
        if (this.mdcPropertyMap != null) {
            throw new IllegalStateException("The MDCPropertyMap has been already set for this event.");
        }
        this.mdcPropertyMap = map;
    }

    @Override
    public Map<String, String> getMdc() {
        return this.getMDCPropertyMap();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        sb.append(this.level).append("] ");
        sb.append(this.getFormattedMessage());
        return sb.toString();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        throw new UnsupportedOperationException(this.getClass() + " does not support serialization. " + "Use LoggerEventVO instance instead. See also LoggerEventVO.build method.");
    }
}

