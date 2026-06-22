/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http2;

import io.netty.handler.codec.http2.Http2StreamFrame;

public interface Http2ResetFrame
extends Http2StreamFrame {
    public long errorCode();
}

