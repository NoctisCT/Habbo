/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http2;

import io.netty.handler.codec.http2.Http2Settings;

public interface Http2SettingsReceivedConsumer {
    public void consumeReceivedSettings(Http2Settings var1);
}

