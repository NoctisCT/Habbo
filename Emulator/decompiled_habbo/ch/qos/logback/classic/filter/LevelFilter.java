/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.AbstractMatcherFilter;
import ch.qos.logback.core.spi.FilterReply;

public class LevelFilter
extends AbstractMatcherFilter<ILoggingEvent> {
    Level level;

    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (!this.isStarted()) {
            return FilterReply.NEUTRAL;
        }
        if (event.getLevel().equals(this.level)) {
            return this.onMatch;
        }
        return this.onMismatch;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    @Override
    public void start() {
        if (this.level != null) {
            super.start();
        }
    }
}

