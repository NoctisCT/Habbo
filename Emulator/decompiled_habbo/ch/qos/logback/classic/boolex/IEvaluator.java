/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.boolex;

import ch.qos.logback.classic.spi.ILoggingEvent;

public interface IEvaluator {
    public boolean doEvaluate(ILoggingEvent var1);
}

