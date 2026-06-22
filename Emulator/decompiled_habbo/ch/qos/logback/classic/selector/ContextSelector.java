/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.selector;

import ch.qos.logback.classic.LoggerContext;
import java.util.List;

public interface ContextSelector {
    public LoggerContext getLoggerContext();

    public LoggerContext getLoggerContext(String var1);

    public LoggerContext getDefaultLoggerContext();

    public LoggerContext detachLoggerContext(String var1);

    public List<String> getContextNames();
}

