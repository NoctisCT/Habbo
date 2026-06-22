/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.net.ssl;

public interface SSLConfigurable {
    public String[] getDefaultProtocols();

    public String[] getSupportedProtocols();

    public void setEnabledProtocols(String[] var1);

    public String[] getDefaultCipherSuites();

    public String[] getSupportedCipherSuites();

    public void setEnabledCipherSuites(String[] var1);

    public void setNeedClientAuth(boolean var1);

    public void setWantClientAuth(boolean var1);
}

