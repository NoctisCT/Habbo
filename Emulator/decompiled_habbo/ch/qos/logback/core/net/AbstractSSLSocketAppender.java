/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.net;

import ch.qos.logback.core.net.AbstractSocketAppender;
import ch.qos.logback.core.net.ssl.ConfigurableSSLSocketFactory;
import ch.qos.logback.core.net.ssl.SSLComponent;
import ch.qos.logback.core.net.ssl.SSLConfiguration;
import ch.qos.logback.core.net.ssl.SSLParametersConfiguration;
import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;

public abstract class AbstractSSLSocketAppender<E>
extends AbstractSocketAppender<E>
implements SSLComponent {
    private SSLConfiguration ssl;
    private SocketFactory socketFactory;

    protected AbstractSSLSocketAppender() {
    }

    @Override
    protected SocketFactory getSocketFactory() {
        return this.socketFactory;
    }

    @Override
    public void start() {
        try {
            SSLContext sslContext = this.getSsl().createContext(this);
            SSLParametersConfiguration parameters = this.getSsl().getParameters();
            parameters.setContext(this.getContext());
            this.socketFactory = new ConfigurableSSLSocketFactory(parameters, sslContext.getSocketFactory());
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

