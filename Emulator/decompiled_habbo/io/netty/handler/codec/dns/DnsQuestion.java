/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.dns;

import io.netty.handler.codec.dns.DnsRecord;

public interface DnsQuestion
extends DnsRecord {
    @Override
    public long timeToLive();
}

