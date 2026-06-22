/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.spi;

public interface LifeCycle {
    public void start();

    public void stop();

    public boolean isStarted();
}

