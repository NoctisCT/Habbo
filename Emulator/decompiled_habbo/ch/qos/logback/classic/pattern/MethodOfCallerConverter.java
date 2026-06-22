/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class MethodOfCallerConverter
extends ClassicConverter {
    @Override
    public String convert(ILoggingEvent le) {
        StackTraceElement[] cda = le.getCallerData();
        if (cda != null && cda.length > 0) {
            return cda[0].getMethodName();
        }
        return "?";
    }
}

