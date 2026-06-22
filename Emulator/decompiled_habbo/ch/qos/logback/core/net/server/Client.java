/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.net.server;

import java.io.Closeable;

public interface Client
extends Runnable,
Closeable {
    @Override
    public void close();
}

