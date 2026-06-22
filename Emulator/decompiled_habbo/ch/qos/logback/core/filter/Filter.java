/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.filter;

import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.spi.LifeCycle;

public abstract class Filter<E>
extends ContextAwareBase
implements LifeCycle {
    private String name;
    boolean start = false;

    @Override
    public void start() {
        this.start = true;
    }

    @Override
    public boolean isStarted() {
        return this.start;
    }

    @Override
    public void stop() {
        this.start = false;
    }

    public abstract FilterReply decide(E var1);

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

