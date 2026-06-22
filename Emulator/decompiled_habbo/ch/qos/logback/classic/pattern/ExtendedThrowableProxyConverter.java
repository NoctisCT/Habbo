/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;

public class ExtendedThrowableProxyConverter
extends ThrowableProxyConverter {
    @Override
    protected void extraData(StringBuilder builder, StackTraceElementProxy step) {
        ThrowableProxyUtil.subjoinPackagingData(builder, step);
    }

    protected void prepareLoggingEvent(ILoggingEvent event) {
    }
}

