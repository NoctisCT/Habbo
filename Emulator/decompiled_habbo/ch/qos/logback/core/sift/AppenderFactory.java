/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.sift;

import ch.qos.logback.core.Appender;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.spi.JoranException;

public interface AppenderFactory<E> {
    public Appender<E> buildAppender(Context var1, String var2) throws JoranException;
}

