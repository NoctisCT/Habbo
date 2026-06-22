/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.net.server;

import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.net.server.ClientVisitor;
import ch.qos.logback.core.net.server.RemoteReceiverClient;
import ch.qos.logback.core.net.server.RemoteReceiverServerListener;
import ch.qos.logback.core.net.server.RemoteReceiverServerRunner;
import ch.qos.logback.core.net.server.ServerListener;
import ch.qos.logback.core.net.server.ServerRunner;
import ch.qos.logback.core.spi.PreSerializationTransformer;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.concurrent.Executor;
import javax.net.ServerSocketFactory;

public abstract class AbstractServerSocketAppender<E>
extends AppenderBase<E> {
    public static final int DEFAULT_BACKLOG = 50;
    public static final int DEFAULT_CLIENT_QUEUE_SIZE = 100;
    private int port = 4560;
    private int backlog = 50;
    private int clientQueueSize = 100;
    private String address;
    private ServerRunner<RemoteReceiverClient> runner;

    @Override
    public void start() {
        if (this.isStarted()) {
            return;
        }
        try {
            ServerSocket socket = this.getServerSocketFactory().createServerSocket(this.getPort(), this.getBacklog(), this.getInetAddress());
            ServerListener<RemoteReceiverClient> listener = this.createServerListener(socket);
            this.runner = this.createServerRunner(listener, this.getContext().getExecutorService());
            this.runner.setContext(this.getContext());
            this.getContext().getExecutorService().execute(this.runner);
            super.start();
        }
        catch (Exception ex) {
            this.addError("server startup error: " + ex, ex);
        }
    }

    protected ServerListener<RemoteReceiverClient> createServerListener(ServerSocket socket) {
        return new RemoteReceiverServerListener(socket);
    }

    protected ServerRunner<RemoteReceiverClient> createServerRunner(ServerListener<RemoteReceiverClient> listener, Executor executor) {
        return new RemoteReceiverServerRunner(listener, executor, this.getClientQueueSize());
    }

    @Override
    public void stop() {
        if (!this.isStarted()) {
            return;
        }
        try {
            this.runner.stop();
            super.stop();
        }
        catch (IOException ex) {
            this.addError("server shutdown error: " + ex, ex);
        }
    }

    @Override
    protected void append(E event) {
        if (event == null) {
            return;
        }
        this.postProcessEvent(event);
        final Serializable serEvent = this.getPST().transform(event);
        this.runner.accept(new ClientVisitor<RemoteReceiverClient>(){

            @Override
            public void visit(RemoteReceiverClient client) {
                client.offer(serEvent);
            }
        });
    }

    protected abstract void postProcessEvent(E var1);

    protected abstract PreSerializationTransformer<E> getPST();

    protected ServerSocketFactory getServerSocketFactory() throws Exception {
        return ServerSocketFactory.getDefault();
    }

    protected InetAddress getInetAddress() throws UnknownHostException {
        if (this.getAddress() == null) {
            return null;
        }
        return InetAddress.getByName(this.getAddress());
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getBacklog() {
        return this.backlog;
    }

    public void setBacklog(int backlog) {
        this.backlog = backlog;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getClientQueueSize() {
        return this.clientQueueSize;
    }

    public void setClientQueueSize(int clientQueueSize) {
        this.clientQueueSize = clientQueueSize;
    }
}

