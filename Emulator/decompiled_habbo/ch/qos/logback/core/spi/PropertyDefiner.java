/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.spi;

import ch.qos.logback.core.spi.ContextAware;

public interface PropertyDefiner
extends ContextAware {
    public String getPropertyValue();
}

