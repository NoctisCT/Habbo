/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.pattern.NamedConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class LoggerConverter
extends NamedConverter {
    @Override
    protected String getFullyQualifiedName(ILoggingEvent event) {
        return event.getLoggerName();
    }
}

