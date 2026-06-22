/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http2;

import io.netty.handler.codec.http2.DefaultHttp2SettingsAckFrame;
import io.netty.handler.codec.http2.Http2Frame;

public interface Http2SettingsAckFrame
extends Http2Frame {
    public static final Http2SettingsAckFrame INSTANCE = new DefaultHttp2SettingsAckFrame();

    @Override
    public String name();
}

