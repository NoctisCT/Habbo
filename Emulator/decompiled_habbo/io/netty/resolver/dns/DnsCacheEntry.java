/*
 * Decompiled with CFR 0.152.
 */
package io.netty.resolver.dns;

import java.net.InetAddress;

public interface DnsCacheEntry {
    public InetAddress address();

    public Throwable cause();
}

