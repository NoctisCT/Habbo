/*
 * Decompiled with CFR 0.152.
 */
package io.netty.resolver.dns;

import java.net.InetSocketAddress;

public interface DnsServerAddressStream {
    public InetSocketAddress next();

    public int size();

    public DnsServerAddressStream duplicate();
}

