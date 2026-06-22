/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.boolex;

import ch.qos.logback.core.boolex.EventEvaluator;
import ch.qos.logback.core.spi.ContextAwareBase;

public abstract class EventEvaluatorBase<E>
extends ContextAwareBase
implements EventEvaluator<E> {
    String name;
    boolean started;

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        if (this.name != null) {
            throw new IllegalStateException("name has been already set");
        }
        this.name = name;
    }

    @Override
    public boolean isStarted() {
        return this.started;
    }

    @Override
    public void start() {
        this.started = true;
    }

    @Override
    public void stop() {
        this.started = false;
    }
}

