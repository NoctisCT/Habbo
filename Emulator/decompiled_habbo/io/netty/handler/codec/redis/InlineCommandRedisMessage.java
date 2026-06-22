/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.redis;

import io.netty.handler.codec.redis.AbstractStringRedisMessage;

public final class InlineCommandRedisMessage
extends AbstractStringRedisMessage {
    public InlineCommandRedisMessage(String content) {
        super(content);
    }
}

