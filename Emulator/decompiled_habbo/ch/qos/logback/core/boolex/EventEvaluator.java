/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.boolex;

import ch.qos.logback.core.boolex.EvaluationException;
import ch.qos.logback.core.spi.ContextAware;
import ch.qos.logback.core.spi.LifeCycle;

public interface EventEvaluator<E>
extends ContextAware,
LifeCycle {
    public boolean evaluate(E var1) throws NullPointerException, EvaluationException;

    public String getName();

    public void setName(String var1);
}

