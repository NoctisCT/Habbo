/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.spi;

import ch.qos.logback.classic.spi.StackTraceElementProxy;

public interface IThrowableProxy {
    public String getMessage();

    public String getClassName();

    public StackTraceElementProxy[] getStackTraceElementProxyArray();

    public int getCommonFrames();

    public IThrowableProxy getCause();

    public IThrowableProxy[] getSuppressed();
}

