/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.spi;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.spi.ContextAware;

public interface Configurator
extends ContextAware {
    public void configure(LoggerContext var1);
}

