/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.net.server;

import ch.qos.logback.core.net.server.Client;
import ch.qos.logback.core.net.server.ClientVisitor;
import ch.qos.logback.core.spi.ContextAware;
import java.io.IOException;

public interface ServerRunner<T extends Client>
extends ContextAware,
Runnable {
    public boolean isRunning();

    public void stop() throws IOException;

    public void accept(ClientVisitor<T> var1);
}

