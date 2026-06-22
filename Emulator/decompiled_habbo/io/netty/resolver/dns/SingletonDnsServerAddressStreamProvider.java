/*
 * Decompiled with CFR 0.152.
 */
package io.netty.resolver.dns;

import io.netty.resolver.dns.DnsServerAddresses;
import io.netty.resolver.dns.UniSequentialDnsServerAddressStreamProvider;
import java.net.InetSocketAddress;

public final class SingletonDnsServerAddressStreamProvider
extends UniSequentialDnsServerAddressStreamProvider {
    public SingletonDnsServerAddressStreamProvider(InetSocketAddress address) {
        super(DnsServerAddresses.singleton(address));
    }
}

