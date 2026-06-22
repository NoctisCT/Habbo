/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.net;

import java.net.Socket;
import java.util.concurrent.Callable;
import javax.net.SocketFactory;

public interface SocketConnector
extends Callable<Socket> {
    @Override
    public Socket call() throws InterruptedException;

    public void setExceptionHandler(ExceptionHandler var1);

    public void setSocketFactory(SocketFactory var1);

    public static interface ExceptionHandler {
        public void connectionFailed(SocketConnector var1, Exception var2);
    }
}

