/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.classic.net.server;

import ch.qos.logback.classic.net.server.RemoteAppenderClient;
import ch.qos.logback.classic.net.server.RemoteAppenderStreamClient;
import ch.qos.logback.core.net.server.ServerSocketListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

class RemoteAppenderServerListener
extends ServerSocketListener<RemoteAppenderClient> {
    public RemoteAppenderServerListener(ServerSocket serverSocket) {
        super(serverSocket);
    }

    @Override
    protected RemoteAppenderClient createClient(String id, Socket socket) throws IOException {
        return new RemoteAppenderStreamClient(id, socket);
    }
}

