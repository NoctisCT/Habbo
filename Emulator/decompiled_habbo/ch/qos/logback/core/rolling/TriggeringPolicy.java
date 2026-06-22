/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.rolling;

import ch.qos.logback.core.spi.LifeCycle;
import java.io.File;

public interface TriggeringPolicy<E>
extends LifeCycle {
    public boolean isTriggeringEvent(File var1, E var2);
}

