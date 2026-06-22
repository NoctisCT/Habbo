/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.util;

public interface InvocationGate {
    public static final long TIME_UNAVAILABLE = -1L;

    public boolean isTooSoon(long var1);
}

