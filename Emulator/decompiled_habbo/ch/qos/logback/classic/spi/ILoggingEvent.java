/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.spi;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.LoggerContextVO;
import ch.qos.logback.core.spi.DeferredProcessingAware;
import java.util.Map;
import org.slf4j.Marker;

public interface ILoggingEvent
extends DeferredProcessingAware {
    public String getThreadName();

    public Level getLevel();

    public String getMessage();

    public Object[] getArgumentArray();

    public String getFormattedMessage();

    public String getLoggerName();

    public LoggerContextVO getLoggerContextVO();

    public IThrowableProxy getThrowableProxy();

    public StackTraceElement[] getCallerData();

    public boolean hasCallerData();

    public Marker getMarker();

    public Map<String, String> getMDCPropertyMap();

    public Map<String, String> getMdc();

    public long getTimeStamp();

    @Override
    public void prepareForDeferredProcessing();
}

