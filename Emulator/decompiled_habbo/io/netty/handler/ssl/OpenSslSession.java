/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.ssl;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;

interface OpenSslSession
extends SSLSession {
    public void handshakeFinished() throws SSLException;

    public void tryExpandApplicationBufferSize(int var1);
}

