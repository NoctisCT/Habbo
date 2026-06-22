/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.rolling;

import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.rolling.RolloverFailure;
import ch.qos.logback.core.rolling.helper.CompressionMode;
import ch.qos.logback.core.spi.LifeCycle;

public interface RollingPolicy
extends LifeCycle {
    public void rollover() throws RolloverFailure;

    public String getActiveFileName();

    public CompressionMode getCompressionMode();

    public void setParent(FileAppender<?> var1);
}

