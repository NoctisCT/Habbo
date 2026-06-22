/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.net;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.net.ReceiverBase;
import ch.qos.logback.classic.net.server.HardenedLoggingEventInputStream;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.net.DefaultSocketConnector;
import ch.qos.logback.core.net.SocketConnector;
import ch.qos.logback.core.util.CloseUtil;
import java.io.EOFException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import javax.net.SocketFactory;

public class SocketReceiver
extends ReceiverBase
implements Runnable,
SocketConnector.ExceptionHandler {
    private static final int DEFAULT_ACCEPT_CONNECTION_DELAY = 5000;
    private String remoteHost;
    private InetAddress address;
    private int port;
    private int reconnectionDelay;
    private int acceptConnectionTimeout = 5000;
    private String receiverId;
    private volatile Socket socket;
    private Future<Socket> connectorTask;

    @Override
    protected boolean shouldStart() {
        int errorCount = 0;
        if (this.port == 0) {
            ++errorCount;
            this.addError("No port was configured for receiver. For more information, please visit http://logback.qos.ch/codes.html#receiver_no_port");
        }
        if (this.remoteHost == null) {
            ++errorCount;
            this.addError("No host name or address was configured for receiver. For more information, please visit http://logback.qos.ch/codes.html#receiver_no_host");
        }
        if (this.reconnectionDelay == 0) {
            this.reconnectionDelay = 30000;
        }
        if (errorCount == 0) {
            try {
                this.address = InetAddress.getByName(this.remoteHost);
            }
            catch (UnknownHostException ex) {
                this.addError("unknown host: " + this.remoteHost);
                ++errorCount;
            }
        }
        if (errorCount == 0) {
            this.receiverId = "receiver " + this.remoteHost + ":" + this.port + ": ";
        }
        return errorCount == 0;
    }

    @Override
    protected void onStop() {
        if (this.socket != null) {
            CloseUtil.closeQuietly(this.socket);
        }
    }

    @Override
    protected Runnable getRunnableTask() {
        return this;
    }

    @Override
    public void run() {
        try {
            LoggerContext lc = (LoggerContext)this.getContext();
            while (!Thread.currentThread().isInterrupted()) {
                SocketConnector connector = this.createConnector(this.address, this.port, 0, this.reconnectionDelay);
                this.connectorTask = this.activateConnector(connector);
                if (this.connectorTask != null) {
                    this.socket = this.waitForConnectorToReturnASocket();
                    if (this.socket != null) {
                        this.dispatchEvents(lc);
                        continue;
                    }
                }
                break;
            }
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
        this.addInfo("shutting down");
    }

    private SocketConnector createConnector(InetAddress address, int port, int initialDelay, int retryDelay) {
        SocketConnector connector = this.newConnector(address, port, initialDelay, retryDelay);
        connector.setExceptionHandler(this);
        connector.setSocketFactory(this.getSocketFactory());
        return connector;
    }

    private Future<Socket> activateConnector(SocketConnector connector) {
        try {
            return this.getContext().getScheduledExecutorService().submit(connector);
        }
        catch (RejectedExecutionException ex) {
            return null;
        }
    }

    private Socket waitForConnectorToReturnASocket() throws InterruptedException {
        try {
            Socket s = this.connectorTask.get();
            this.connectorTask = null;
            return s;
        }
        catch (ExecutionException e) {
            return null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    private void dispatchEvents(LoggerContext lc) {
        HardenedLoggingEventInputStream ois = null;
        try {
            this.socket.setSoTimeout(this.acceptConnectionTimeout);
            ois = new HardenedLoggingEventInputStream(this.socket.getInputStream());
            this.socket.setSoTimeout(0);
            this.addInfo(this.receiverId + "connection established");
            while (true) {
                ILoggingEvent event;
                Logger remoteLogger;
                if (!(remoteLogger = lc.getLogger((event = (ILoggingEvent)ois.readObject()).getLoggerName())).isEnabledFor(event.getLevel())) {
                    continue;
                }
                remoteLogger.callAppenders(event);
            }
        }
        catch (EOFException ex) {
            this.addInfo(this.receiverId + "end-of-stream detected");
            CloseUtil.closeQuietly(ois);
            CloseUtil.closeQuietly(this.socket);
            this.socket = null;
            this.addInfo(this.receiverId + "connection closed");
        }
        catch (IOException ex) {
            this.addInfo(this.receiverId + "connection failed: " + ex);
            CloseUtil.closeQuietly(ois);
            CloseUtil.closeQuietly(this.socket);
            this.socket = null;
            this.addInfo(this.receiverId + "connection closed");
        }
        catch (ClassNotFoundException ex) {
            this.addInfo(this.receiverId + "unknown event class: " + ex);
            {
                catch (Throwable throwable) {
                    CloseUtil.closeQuietly(ois);
                    CloseUtil.closeQuietly(this.socket);
                    this.socket = null;
                    this.addInfo(this.receiverId + "connection closed");
                    throw throwable;
                }
            }
            CloseUtil.closeQuietly(ois);
            CloseUtil.closeQuietly(this.socket);
            this.socket = null;
            this.addInfo(this.receiverId + "connection closed");
        }
    }

    @Override
    public void connectionFailed(SocketConnector connector, Exception ex) {
        if (ex instanceof InterruptedException) {
            this.addInfo("connector interrupted");
        } else if (ex instanceof ConnectException) {
            this.addInfo(this.receiverId + "connection refused");
        } else {
            this.addInfo(this.receiverId + ex);
        }
    }

    protected SocketConnector newConnector(InetAddress address, int port, int initialDelay, int retryDelay) {
        return new DefaultSocketConnector(address, port, initialDelay, retryDelay);
    }

    protected SocketFactory getSocketFactory() {
        return SocketFactory.getDefault();
    }

    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setReconnectionDelay(int reconnectionDelay) {
        this.reconnectionDelay = reconnectionDelay;
    }

    public void setAcceptConnectionTimeout(int acceptConnectionTimeout) {
        this.acceptConnectionTimeout = acceptConnectionTimeout;
    }
}

