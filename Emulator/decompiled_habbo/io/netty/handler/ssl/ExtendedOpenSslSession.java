/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.OpenSslSession;
import io.netty.util.internal.SuppressJava6Requirement;
import java.security.Principal;
import java.security.cert.Certificate;
import java.util.Collections;
import java.util.List;
import javax.net.ssl.ExtendedSSLSession;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSessionContext;
import javax.security.cert.X509Certificate;

@SuppressJava6Requirement(reason="Usage guarded by java version check")
abstract class ExtendedOpenSslSession
extends ExtendedSSLSession
implements OpenSslSession {
    private static final String[] LOCAL_SUPPORTED_SIGNATURE_ALGORITHMS = new String[]{"SHA512withRSA", "SHA512withECDSA", "SHA384withRSA", "SHA384withECDSA", "SHA256withRSA", "SHA256withECDSA", "SHA224withRSA", "SHA224withECDSA", "SHA1withRSA", "SHA1withECDSA"};
    private final OpenSslSession wrapped;

    ExtendedOpenSslSession(OpenSslSession wrapped) {
        this.wrapped = wrapped;
    }

    public abstract List getRequestedServerNames();

    @Override
    public List<byte[]> getStatusResponses() {
        return Collections.emptyList();
    }

    @Override
    public final void handshakeFinished() throws SSLException {
        this.wrapped.handshakeFinished();
    }

    @Override
    public final void tryExpandApplicationBufferSize(int packetLengthDataOnly) {
        this.wrapped.tryExpandApplicationBufferSize(packetLengthDataOnly);
    }

    @Override
    public final String[] getLocalSupportedSignatureAlgorithms() {
        return (String[])LOCAL_SUPPORTED_SIGNATURE_ALGORITHMS.clone();
    }

    @Override
    public final byte[] getId() {
        return this.wrapped.getId();
    }

    @Override
    public final SSLSessionContext getSessionContext() {
        return this.wrapped.getSessionContext();
    }

    @Override
    public final long getCreationTime() {
        return this.wrapped.getCreationTime();
    }

    @Override
    public final long getLastAccessedTime() {
        return this.wrapped.getLastAccessedTime();
    }

    @Override
    public final void invalidate() {
        this.wrapped.invalidate();
    }

    @Override
    public final boolean isValid() {
        return this.wrapped.isValid();
    }

    @Override
    public final void putValue(String s, Object o) {
        this.wrapped.putValue(s, o);
    }

    @Override
    public final Object getValue(String s) {
        return this.wrapped.getValue(s);
    }

    @Override
    public final void removeValue(String s) {
        this.wrapped.removeValue(s);
    }

    @Override
    public final String[] getValueNames() {
        return this.wrapped.getValueNames();
    }

    @Override
    public final Certificate[] getPeerCertificates() throws SSLPeerUnverifiedException {
        return this.wrapped.getPeerCertificates();
    }

    @Override
    public final Certificate[] getLocalCertificates() {
        return this.wrapped.getLocalCertificates();
    }

    @Override
    public final X509Certificate[] getPeerCertificateChain() throws SSLPeerUnverifiedException {
        return this.wrapped.getPeerCertificateChain();
    }

    @Override
    public final Principal getPeerPrincipal() throws SSLPeerUnverifiedException {
        return this.wrapped.getPeerPrincipal();
    }

    @Override
    public final Principal getLocalPrincipal() {
        return this.wrapped.getLocalPrincipal();
    }

    @Override
    public final String getCipherSuite() {
        return this.wrapped.getCipherSuite();
    }

    @Override
    public String getProtocol() {
        return this.wrapped.getProtocol();
    }

    @Override
    public final String getPeerHost() {
        return this.wrapped.getPeerHost();
    }

    @Override
    public final int getPeerPort() {
        return this.wrapped.getPeerPort();
    }

    @Override
    public final int getPacketBufferSize() {
        return this.wrapped.getPacketBufferSize();
    }

    @Override
    public final int getApplicationBufferSize() {
        return this.wrapped.getApplicationBufferSize();
    }
}

