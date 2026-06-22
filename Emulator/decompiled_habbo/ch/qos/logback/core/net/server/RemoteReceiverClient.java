/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.net.server;

import ch.qos.logback.core.net.server.Client;
import ch.qos.logback.core.spi.ContextAware;
import java.io.Serializable;
import java.util.concurrent.BlockingQueue;

interface RemoteReceiverClient
extends Client,
ContextAware {
    public void setQueue(BlockingQueue<Serializable> var1);

    public boolean offer(Serializable var1);
}

