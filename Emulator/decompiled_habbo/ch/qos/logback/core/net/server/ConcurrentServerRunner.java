/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.net.server;

import ch.qos.logback.core.net.server.Client;
import ch.qos.logback.core.net.server.ClientVisitor;
import ch.qos.logback.core.net.server.ServerListener;
import ch.qos.logback.core.net.server.ServerRunner;
import ch.qos.logback.core.spi.ContextAwareBase;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class ConcurrentServerRunner<T extends Client>
extends ContextAwareBase
implements Runnable,
ServerRunner<T> {
    private final Lock clientsLock = new ReentrantLock();
    private final Collection<T> clients = new ArrayList<T>();
    private final ServerListener<T> listener;
    private final Executor executor;
    private boolean running;

    public ConcurrentServerRunner(ServerListener<T> listener, Executor executor) {
        this.listener = listener;
        this.executor = executor;
    }

    @Override
    public boolean isRunning() {
        return this.running;
    }

    protected void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void stop() throws IOException {
        this.listener.close();
        this.accept(new ClientVisitor<T>(){

            @Override
            public void visit(T client) {
                client.close();
            }
        });
    }

    @Override
    public void accept(ClientVisitor<T> visitor) {
        Collection<T> clients = this.copyClients();
        for (Client client : clients) {
            try {
                visitor.visit(client);
            }
            catch (RuntimeException ex) {
                this.addError(client + ": " + ex);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Collection<T> copyClients() {
        this.clientsLock.lock();
        try {
            ArrayList<T> copy;
            ArrayList<T> arrayList = copy = new ArrayList<T>(this.clients);
            return arrayList;
        }
        finally {
            this.clientsLock.unlock();
        }
    }

    @Override
    public void run() {
        this.setRunning(true);
        try {
            this.addInfo("listening on " + this.listener);
            while (!Thread.currentThread().isInterrupted()) {
                T client = this.listener.acceptClient();
                if (!this.configureClient(client)) {
                    this.addError(client + ": connection dropped");
                    client.close();
                    continue;
                }
                try {
                    this.executor.execute(new ClientWrapper(this, client));
                }
                catch (RejectedExecutionException ex) {
                    this.addError(client + ": connection dropped");
                    client.close();
                }
            }
        }
        catch (InterruptedException client) {
        }
        catch (Exception ex) {
            this.addError("listener: " + ex);
        }
        this.setRunning(false);
        this.addInfo("shutting down");
        this.listener.close();
    }

    protected abstract boolean configureClient(T var1);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void addClient(T client) {
        this.clientsLock.lock();
        try {
            this.clients.add(client);
        }
        finally {
            this.clientsLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void removeClient(T client) {
        this.clientsLock.lock();
        try {
            this.clients.remove(client);
        }
        finally {
            this.clientsLock.unlock();
        }
    }

    private static class ClientWrapper
    implements Client {
        private final T delegate;
        final /* synthetic */ ConcurrentServerRunner this$0;

        public ClientWrapper(T client) {
            this.this$0 = var1_1;
            this.delegate = client;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            this.this$0.addClient(this.delegate);
            try {
                this.delegate.run();
            }
            finally {
                this.this$0.removeClient(this.delegate);
            }
        }

        @Override
        public void close() {
            this.delegate.close();
        }
    }
}

