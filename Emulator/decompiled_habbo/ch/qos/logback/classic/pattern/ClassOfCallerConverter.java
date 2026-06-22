/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.pattern.NamedConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class ClassOfCallerConverter
extends NamedConverter {
    @Override
    protected String getFullyQualifiedName(ILoggingEvent event) {
        StackTraceElement[] cda = event.getCallerData();
        if (cda != null && cda.length > 0) {
            return cda[0].getClassName();
        }
        return "?";
    }
}

