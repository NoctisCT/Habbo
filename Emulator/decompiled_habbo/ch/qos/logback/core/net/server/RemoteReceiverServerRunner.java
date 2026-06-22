/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.net.server;

import ch.qos.logback.core.net.server.ConcurrentServerRunner;
import ch.qos.logback.core.net.server.RemoteReceiverClient;
import ch.qos.logback.core.net.server.ServerListener;
import java.io.Serializable;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;

class RemoteReceiverServerRunner
extends ConcurrentServerRunner<RemoteReceiverClient> {
    private final int clientQueueSize;

    public RemoteReceiverServerRunner(ServerListener<RemoteReceiverClient> listener, Executor executor, int clientQueueSize) {
        super(listener, executor);
        this.clientQueueSize = clientQueueSize;
    }

    @Override
    protected boolean configureClient(RemoteReceiverClient client) {
        client.setContext(this.getContext());
        client.setQueue(new ArrayBlockingQueue<Serializable>(this.clientQueueSize));
        return true;
    }
}

