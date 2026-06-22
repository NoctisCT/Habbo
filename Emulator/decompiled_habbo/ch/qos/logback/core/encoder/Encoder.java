/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.encoder;

import ch.qos.logback.core.spi.ContextAware;
import ch.qos.logback.core.spi.LifeCycle;

public interface Encoder<E>
extends ContextAware,
LifeCycle {
    public byte[] headerBytes();

    public byte[] encode(E var1);

    public byte[] footerBytes();
}

