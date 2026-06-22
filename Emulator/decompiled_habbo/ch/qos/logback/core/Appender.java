/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core;

import ch.qos.logback.core.LogbackException;
import ch.qos.logback.core.spi.ContextAware;
import ch.qos.logback.core.spi.FilterAttachable;
import ch.qos.logback.core.spi.LifeCycle;

public interface Appender<E>
extends LifeCycle,
ContextAware,
FilterAttachable<E> {
    public String getName();

    public void doAppend(E var1) throws LogbackException;

    public void setName(String var1);
}

