/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.spi;

import java.io.Serializable;

public interface PreSerializationTransformer<E> {
    public Serializable transform(E var1);
}

