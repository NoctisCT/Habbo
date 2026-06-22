/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.ssl;

import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.ssl.OpenSsl;
import io.netty.handler.ssl.OpenSslContext;
import io.netty.handler.ssl.ReferenceCountedOpenSslEngine;

public final class OpenSslEngine
extends ReferenceCountedOpenSslEngine {
    OpenSslEngine(OpenSslContext context, ByteBufAllocator alloc, String peerHost, int peerPort, boolean jdkCompatibilityMode) {
        super(context, alloc, peerHost, peerPort, jdkCompatibilityMode, false);
    }

    protected void finalize() throws Throwable {
        super.finalize();
        OpenSsl.releaseIfNeeded(this);
    }
}

