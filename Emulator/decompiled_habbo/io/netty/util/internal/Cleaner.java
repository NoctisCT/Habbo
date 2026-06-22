/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.internal;

import java.nio.ByteBuffer;

interface Cleaner {
    public void freeDirectBuffer(ByteBuffer var1);
}

