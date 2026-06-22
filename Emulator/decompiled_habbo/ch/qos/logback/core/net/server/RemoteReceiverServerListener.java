/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.net.server;

import ch.qos.logback.core.net.server.RemoteReceiverClient;
import ch.qos.logback.core.net.server.RemoteReceiverStreamClient;
import ch.qos.logback.core.net.server.ServerSocketListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

class RemoteReceiverServerListener
extends ServerSocketListener<RemoteReceiverClient> {
    public RemoteReceiverServerListener(ServerSocket serverSocket) {
        super(serverSocket);
    }

    @Override
    protected RemoteReceiverClient createClient(String id, Socket socket) throws IOException {
        return new RemoteReceiverStreamClient(id, socket);
    }
}

