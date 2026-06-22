/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.net.ssl;

import ch.qos.logback.core.net.ssl.SSLConfigurableServerSocket;
import ch.qos.logback.core.net.ssl.SSLParametersConfiguration;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

public class ConfigurableSSLServerSocketFactory
extends ServerSocketFactory {
    private final SSLParametersConfiguration parameters;
    private final SSLServerSocketFactory delegate;

    public ConfigurableSSLServerSocketFactory(SSLParametersConfiguration parameters, SSLServerSocketFactory delegate) {
        this.parameters = parameters;
        this.delegate = delegate;
    }

    @Override
    public ServerSocket createServerSocket(int port, int backlog, InetAddress ifAddress) throws IOException {
        SSLServerSocket socket = (SSLServerSocket)this.delegate.createServerSocket(port, backlog, ifAddress);
        this.parameters.configure(new SSLConfigurableServerSocket(socket));
        return socket;
    }

    @Override
    public ServerSocket createServerSocket(int port, int backlog) throws IOException {
        SSLServerSocket socket = (SSLServerSocket)this.delegate.createServerSocket(port, backlog);
        this.parameters.configure(new SSLConfigurableServerSocket(socket));
        return socket;
    }

    @Override
    public ServerSocket createServerSocket(int port) throws IOException {
        SSLServerSocket socket = (SSLServerSocket)this.delegate.createServerSocket(port);
        this.parameters.configure(new SSLConfigurableServerSocket(socket));
        return socket;
    }
}

