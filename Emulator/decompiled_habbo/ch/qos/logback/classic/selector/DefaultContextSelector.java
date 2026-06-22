/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.selector;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.selector.ContextSelector;
import java.util.Arrays;
import java.util.List;

public class DefaultContextSelector
implements ContextSelector {
    private LoggerContext defaultLoggerContext;

    public DefaultContextSelector(LoggerContext context) {
        this.defaultLoggerContext = context;
    }

    @Override
    public LoggerContext getLoggerContext() {
        return this.getDefaultLoggerContext();
    }

    @Override
    public LoggerContext getDefaultLoggerContext() {
        return this.defaultLoggerContext;
    }

    @Override
    public LoggerContext detachLoggerContext(String loggerContextName) {
        return this.defaultLoggerContext;
    }

    @Override
    public List<String> getContextNames() {
        return Arrays.asList(this.defaultLoggerContext.getName());
    }

    @Override
    public LoggerContext getLoggerContext(String name) {
        if (this.defaultLoggerContext.getName().equals(name)) {
            return this.defaultLoggerContext;
        }
        return null;
    }
}

