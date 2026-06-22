/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.net.server;

import ch.qos.logback.core.net.server.Client;
import java.io.Closeable;
import java.io.IOException;

public interface ServerListener<T extends Client>
extends Closeable {
    public T acceptClient() throws IOException, InterruptedException;

    @Override
    public void close();
}

