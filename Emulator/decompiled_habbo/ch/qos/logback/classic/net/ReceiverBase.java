/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.net;

import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.LifeCycle;

public abstract class ReceiverBase
extends ContextAwareBase
implements LifeCycle {
    private boolean started;

    @Override
    public final void start() {
        if (this.isStarted()) {
            return;
        }
        if (this.getContext() == null) {
            throw new IllegalStateException("context not set");
        }
        if (this.shouldStart()) {
            this.getContext().getScheduledExecutorService().execute(this.getRunnableTask());
            this.started = true;
        }
    }

    @Override
    public final void stop() {
        if (!this.isStarted()) {
            return;
        }
        try {
            this.onStop();
        }
        catch (RuntimeException ex) {
            this.addError("on stop: " + ex, ex);
        }
        this.started = false;
    }

    @Override
    public final boolean isStarted() {
        return this.started;
    }

    protected abstract boolean shouldStart();

    protected abstract void onStop();

    protected abstract Runnable getRunnableTask();
}

