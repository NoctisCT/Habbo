/*
 * Decompiled with CFR 0.152.
 */
package io.netty.resolver.dns;

import io.netty.channel.EventLoop;
import io.netty.resolver.dns.DnsCnameCache;

public final class NoopDnsCnameCache
implements DnsCnameCache {
    public static final NoopDnsCnameCache INSTANCE = new NoopDnsCnameCache();

    private NoopDnsCnameCache() {
    }

    @Override
    public String get(String hostname) {
        return null;
    }

    @Override
    public void cache(String hostname, String cname, long originalTtl, EventLoop loop) {
    }

    @Override
    public void clear() {
    }

    @Override
    public boolean clear(String hostname) {
        return false;
    }
}

