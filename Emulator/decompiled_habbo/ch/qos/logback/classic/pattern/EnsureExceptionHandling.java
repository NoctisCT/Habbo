/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.pattern.ExtendedThrowableProxyConverter;
import ch.qos.logback.classic.pattern.ThrowableHandlingConverter;
import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.pattern.Converter;
import ch.qos.logback.core.pattern.ConverterUtil;
import ch.qos.logback.core.pattern.PostCompileProcessor;

public class EnsureExceptionHandling
implements PostCompileProcessor<ILoggingEvent> {
    @Override
    public void process(Context context, Converter<ILoggingEvent> head) {
        if (head == null) {
            throw new IllegalArgumentException("cannot process empty chain");
        }
        if (!this.chainHandlesThrowable(head)) {
            Converter<ILoggingEvent> tail = ConverterUtil.findTail(head);
            ThrowableProxyConverter exConverter = null;
            LoggerContext loggerContext = (LoggerContext)context;
            exConverter = loggerContext.isPackagingDataEnabled() ? new ExtendedThrowableProxyConverter() : new ThrowableProxyConverter();
            tail.setNext(exConverter);
        }
    }

    public boolean chainHandlesThrowable(Converter<ILoggingEvent> head) {
        for (Converter<ILoggingEvent> c = head; c != null; c = c.getNext()) {
            if (!(c instanceof ThrowableHandlingConverter)) continue;
            return true;
        }
        return false;
    }
}

