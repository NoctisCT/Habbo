/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.net.ssl;

import ch.qos.logback.core.net.ssl.SSLConfigurable;
import javax.net.ssl.SSLServerSocket;

public class SSLConfigurableServerSocket
implements SSLConfigurable {
    private final SSLServerSocket delegate;

    public SSLConfigurableServerSocket(SSLServerSocket delegate) {
        this.delegate = delegate;
    }

    @Override
    public String[] getDefaultProtocols() {
        return this.delegate.getEnabledProtocols();
    }

    @Override
    public String[] getSupportedProtocols() {
        return this.delegate.getSupportedProtocols();
    }

    @Override
    public void setEnabledProtocols(String[] protocols) {
        this.delegate.setEnabledProtocols(protocols);
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return this.delegate.getEnabledCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return this.delegate.getSupportedCipherSuites();
    }

    @Override
    public void setEnabledCipherSuites(String[] suites) {
        this.delegate.setEnabledCipherSuites(suites);
    }

    @Override
    public void setNeedClientAuth(boolean state) {
        this.delegate.setNeedClientAuth(state);
    }

    @Override
    public void setWantClientAuth(boolean state) {
        this.delegate.setWantClientAuth(state);
    }
}

