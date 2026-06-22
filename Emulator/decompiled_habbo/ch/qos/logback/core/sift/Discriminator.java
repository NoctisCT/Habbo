/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.sift;

import ch.qos.logback.core.spi.LifeCycle;

public interface Discriminator<E>
extends LifeCycle {
    public String getDiscriminatingValue(E var1);

    public String getKey();
}

