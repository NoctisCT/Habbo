/*
 * Decompiled with CFR 0.152.
 */
package com.mysql.cj.protocol;

public interface PacketReceivedTimeHolder {
    default public long getLastPacketReceivedTime() {
        return 0L;
    }
}

