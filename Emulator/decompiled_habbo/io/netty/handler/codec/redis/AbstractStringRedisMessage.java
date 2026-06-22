/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.redis;

import io.netty.handler.codec.redis.RedisMessage;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;

public abstract class AbstractStringRedisMessage
implements RedisMessage {
    private final String content;

    AbstractStringRedisMessage(String content) {
        this.content = ObjectUtil.checkNotNull(content, "content");
    }

    public final String content() {
        return this.content;
    }

    public String toString() {
        return StringUtil.simpleClassName(this) + '[' + "content=" + this.content + ']';
    }
}

