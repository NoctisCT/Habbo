/*
 * Decompiled with CFR 0.152.
 */
package com.mysql.cj.protocol;

public interface Message {
    public byte[] getByteBuffer();

    public int getPosition();
}

