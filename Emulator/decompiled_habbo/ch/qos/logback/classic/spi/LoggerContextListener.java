/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.spi;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

public interface LoggerContextListener {
    public boolean isResetResistant();

    public void onStart(LoggerContext var1);

    public void onReset(LoggerContext var1);

    public void onStop(LoggerContext var1);

    public void onLevelChange(Logger var1, Level var2);
}

