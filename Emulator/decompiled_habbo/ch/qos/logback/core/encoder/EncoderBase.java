/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.encoder;

import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.spi.ContextAwareBase;

public abstract class EncoderBase<E>
extends ContextAwareBase
implements Encoder<E> {
    protected boolean started;

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

