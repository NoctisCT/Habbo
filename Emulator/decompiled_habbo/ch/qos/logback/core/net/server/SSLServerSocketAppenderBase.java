/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.net.server;

import ch.qos.logback.core.net.server.AbstractServerSocketAppender;
import ch.qos.logback.core.net.ssl.ConfigurableSSLServerSocketFactory;
import ch.qos.logback.core.net.ssl.SSLComponent;
import ch.qos.logback.core.net.ssl.SSLConfiguration;
import ch.qos.logback.core.net.ssl.SSLParametersConfiguration;
import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLContext;

public abstract class SSLServerSocketAppenderBase<E>
extends AbstractServerSocketAppender<E>
implements SSLComponent {
    private SSLConfiguration ssl;
    private ServerSocketFactory socketFactory;

    @Override
    protected ServerSocketFactory getServerSocketFactory() {
        return this.socketFactory;
    }

    @Override
    public void start() {
        try {
            SSLContext sslContext = this.getSsl().createContext(this);
            SSLParametersConfiguration parameters = this.getSsl().getParameters();
            parameters.setContext(this.getContext());
            this.socketFactory = new ConfigurableSSLServerSocketFactory(parameters, sslContext.getServerSocketFactory());
            super.start();
        }
        catch (Exception ex) {
            this.addError(ex.getMessage(), ex);
        }
    }

    @Override
    public SSLConfiguration getSsl() {
        if (this.ssl == null) {
            this.ssl = new SSLConfiguration();
        }
        return this.ssl;
    }

    @Override
    public void setSsl(SSLConfiguration ssl) {
        this.ssl = ssl;
    }
}

