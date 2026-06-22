/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.jul;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.jul.JULHelper;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.LifeCycle;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.LogManager;

public class LevelChangePropagator
extends ContextAwareBase
implements LoggerContextListener,
LifeCycle {
    private Set<java.util.logging.Logger> julLoggerSet = new HashSet<java.util.logging.Logger>();
    boolean isStarted = false;
    boolean resetJUL = false;

    public void setResetJUL(boolean resetJUL) {
        this.resetJUL = resetJUL;
    }

    @Override
    public boolean isResetResistant() {
        return false;
    }

    @Override
    public void onStart(LoggerContext context) {
    }

    @Override
    public void onReset(LoggerContext context) {
    }

    @Override
    public void onStop(LoggerContext context) {
    }

    @Override
    public void onLevelChange(Logger logger, Level level) {
        this.propagate(logger, level);
    }

    private void propagate(Logger logger, Level level) {
        this.addInfo("Propagating " + level + " level on " + logger + " onto the JUL framework");
        java.util.logging.Logger julLogger = JULHelper.asJULLogger(logger);
        this.julLoggerSet.add(julLogger);
        java.util.logging.Level julLevel = JULHelper.asJULLevel(level);
        julLogger.setLevel(julLevel);
    }

    public void resetJULLevels() {
        LogManager lm = LogManager.getLogManager();
        Enumeration<String> e = lm.getLoggerNames();
        while (e.hasMoreElements()) {
            String loggerName = e.nextElement();
            java.util.logging.Logger julLogger = lm.getLogger(loggerName);
            if (!JULHelper.isRegularNonRootLogger(julLogger) || julLogger.getLevel() == null) continue;
            this.addInfo("Setting level of jul logger [" + loggerName + "] to null");
            julLogger.setLevel(null);
        }
    }

    private void propagateExistingLoggerLevels() {
        LoggerContext loggerContext = (LoggerContext)this.context;
        List<Logger> loggerList = loggerContext.getLoggerList();
        for (Logger l : loggerList) {
            if (l.getLevel() == null) continue;
            this.propagate(l, l.getLevel());
        }
    }

    @Override
    public void start() {
        if (this.resetJUL) {
            this.resetJULLevels();
        }
        this.propagateExistingLoggerLevels();
        this.isStarted = true;
    }

    @Override
    public void stop() {
        this.isStarted = false;
    }

    @Override
    public boolean isStarted() {
        return this.isStarted;
    }
}

