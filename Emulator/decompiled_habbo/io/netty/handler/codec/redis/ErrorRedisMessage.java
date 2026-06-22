/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.redis;

import io.netty.handler.codec.redis.AbstractStringRedisMessage;

public final class ErrorRedisMessage
extends AbstractStringRedisMessage {
    public ErrorRedisMessage(String content) {
        super(content);
    }
}

