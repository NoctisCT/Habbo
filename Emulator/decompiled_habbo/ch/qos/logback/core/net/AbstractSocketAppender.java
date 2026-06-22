/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.net;

import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.net.AutoFlushingObjectWriter;
import ch.qos.logback.core.net.DefaultSocketConnector;
import ch.qos.logback.core.net.ObjectWriter;
import ch.qos.logback.core.net.ObjectWriterFactory;
import ch.qos.logback.core.net.QueueFactory;
import ch.qos.logback.core.net.SocketConnector;
import ch.qos.logback.core.spi.PreSerializationTransformer;
import ch.qos.logback.core.util.CloseUtil;
import ch.qos.logback.core.util.Duration;
import java.io.IOException;
import java.io.Serializable;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.net.SocketFactory;

public abstract class AbstractSocketAppender<E>
extends AppenderBase<E>
implements SocketConnector.ExceptionHandler {
    public static final int DEFAULT_PORT = 4560;
    public static final int DEFAULT_RECONNECTION_DELAY = 30000;
    public static final int DEFAULT_QUEUE_SIZE = 128;
    private static final int DEFAULT_ACCEPT_CONNECTION_DELAY = 5000;
    private static final int DEFAULT_EVENT_DELAY_TIMEOUT = 100;
    private final ObjectWriterFactory objectWriterFactory;
    private final QueueFactory queueFactory;
    private String remoteHost;
    private int port = 4560;
    private InetAddress address;
    private Duration reconnectionDelay = new Duration(30000L);
    private int queueSize = 128;
    private int acceptConnectionTimeout = 5000;
    private Duration eventDelayLimit = new Duration(100L);
    private BlockingDeque<E> deque;
    private String peerId;
    private SocketConnector connector;
    private Future<?> task;
    private volatile Socket socket;

    protected AbstractSocketAppender() {
        this(new QueueFactory(), new ObjectWriterFactory());
    }

    AbstractSocketAppender(QueueFactory queueFactory, ObjectWriterFactory objectWriterFactory) {
        this.objectWriterFactory = objectWriterFactory;
        this.queueFactory = queueFactory;
    }

    @Override
    public void start() {
        if (this.isStarted()) {
            return;
        }
        int errorCount = 0;
        if (this.port <= 0) {
            ++errorCount;
            this.addError("No port was configured for appender" + this.name + " For more information, please visit http://logback.qos.ch/codes.html#socket_no_port");
        }
        if (this.remoteHost == null) {
            ++errorCount;
            this.addError("No remote host was configured for appender" + this.name + " For more information, please visit http://logback.qos.ch/codes.html#socket_no_host");
        }
        if (this.queueSize == 0) {
            this.addWarn("Queue size of zero is deprecated, use a size of one to indicate synchronous processing");
        }
        if (this.queueSize < 0) {
            ++errorCount;
            this.addError("Queue size must be greater than zero");
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
            this.deque = this.queueFactory.newLinkedBlockingDeque(this.queueSize);
            this.peerId = "remote peer " + this.remoteHost + ":" + this.port + ": ";
            this.connector = this.createConnector(this.address, this.port, 0, this.reconnectionDelay.getMilliseconds());
            this.task = this.getContext().getExecutorService().submit(new Runnable(){

                @Override
                public void run() {
                    AbstractSocketAppender.this.connectSocketAndDispatchEvents();
                }
            });
            super.start();
        }
    }

    @Override
    public void stop() {
        if (!this.isStarted()) {
            return;
        }
        CloseUtil.closeQuietly(this.socket);
        this.task.cancel(true);
        super.stop();
    }

    @Override
    protected void append(E event) {
        if (event == null || !this.isStarted()) {
            return;
        }
        try {
            boolean inserted = this.deque.offer(event, this.eventDelayLimit.getMilliseconds(), TimeUnit.MILLISECONDS);
            if (!inserted) {
                this.addInfo("Dropping event due to timeout limit of [" + this.eventDelayLimit + "] being exceeded");
            }
        }
        catch (InterruptedException e) {
            this.addError("Interrupted while appending event to SocketAppender", e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void connectSocketAndDispatchEvents() {
        try {
            while (this.socketConnectionCouldBeEstablished()) {
                try {
                    ObjectWriter objectWriter = this.createObjectWriterForSocket();
                    this.addInfo(this.peerId + "connection established");
                    this.dispatchEvents(objectWriter);
                }
                catch (IOException ex) {
                    this.addInfo(this.peerId + "connection failed: " + ex);
                }
                finally {
                    CloseUtil.closeQuietly(this.socket);
                    this.socket = null;
                    this.addInfo(this.peerId + "connection closed");
                }
            }
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
        this.addInfo("shutting down");
    }

    private boolean socketConnectionCouldBeEstablished() throws InterruptedException {
        this.socket = this.connector.call();
        return this.socket != null;
    }

    private ObjectWriter createObjectWriterForSocket() throws IOException {
        this.socket.setSoTimeout(this.acceptConnectionTimeout);
        AutoFlushingObjectWriter objectWriter = this.objectWriterFactory.newAutoFlushingObjectWriter(this.socket.getOutputStream());
        this.socket.setSoTimeout(0);
        return objectWriter;
    }

    private SocketConnector createConnector(InetAddress address, int port, int initialDelay, long retryDelay) {
        SocketConnector connector = this.newConnector(address, port, initialDelay, retryDelay);
        connector.setExceptionHandler(this);
        connector.setSocketFactory(this.getSocketFactory());
        return connector;
    }

    private void dispatchEvents(ObjectWriter objectWriter) throws InterruptedException, IOException {
        while (true) {
            E event = this.deque.takeFirst();
            this.postProcessEvent(event);
            Serializable serializableEvent = this.getPST().transform(event);
            try {
                objectWriter.write(serializableEvent);
            }
            catch (IOException e) {
                this.tryReAddingEventToFrontOfQueue(event);
                throw e;
            }
        }
    }

    private void tryReAddingEventToFrontOfQueue(E event) {
        boolean wasInserted = this.deque.offerFirst(event);
        if (!wasInserted) {
            this.addInfo("Dropping event due to socket connection error and maxed out deque capacity");
        }
    }

    @Override
    public void connectionFailed(SocketConnector connector, Exception ex) {
        if (ex instanceof InterruptedException) {
            this.addInfo("connector interrupted");
        } else if (ex instanceof ConnectException) {
            this.addInfo(this.peerId + "connection refused");
        } else {
            this.addInfo(this.peerId + ex);
        }
    }

    protected SocketConnector newConnector(InetAddress address, int port, long initialDelay, long retryDelay) {
        return new DefaultSocketConnector(address, port, initialDelay, retryDelay);
    }

    protected SocketFactory getSocketFactory() {
        return SocketFactory.getDefault();
    }

    protected abstract void postProcessEvent(E var1);

    protected abstract PreSerializationTransformer<E> getPST();

    public void setRemoteHost(String host) {
        this.remoteHost = host;
    }

    public String getRemoteHost() {
        return this.remoteHost;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return this.port;
    }

    public void setReconnectionDelay(Duration delay) {
        this.reconnectionDelay = delay;
    }

    public Duration getReconnectionDelay() {
        return this.reconnectionDelay;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public int getQueueSize() {
        return this.queueSize;
    }

    public void setEventDelayLimit(Duration eventDelayLimit) {
        this.eventDelayLimit = eventDelayLimit;
    }

    public Duration getEventDelayLimit() {
        return this.eventDelayLimit;
    }

    void setAcceptConnectionTimeout(int acceptConnectionTimeout) {
        this.acceptConnectionTimeout = acceptConnectionTimeout;
    }
}

