/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.queues.BaseMpscLinkedArrayQueue;
import io.netty.util.internal.shaded.org.jctools.queues.LinkedArrayQueueUtil;
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueueUtil;

public class MpscUnboundedArrayQueue<E>
extends BaseMpscLinkedArrayQueue<E> {
    long p0;
    long p1;
    long p2;
    long p3;
    long p4;
    long p5;
    long p6;
    long p7;
    long p10;
    long p11;
    long p12;
    long p13;
    long p14;
    long p15;
    long p16;
    long p17;

    public MpscUnboundedArrayQueue(int chunkSize) {
        super(chunkSize);
    }

    @Override
    protected long availableInQueue(long pIndex, long cIndex) {
        return Integer.MAX_VALUE;
    }

    @Override
    public int capacity() {
        return -1;
    }

    @Override
    public int drain(MessagePassingQueue.Consumer<E> c) {
        return this.drain((MessagePassingQueue.Consumer)c, 4096);
    }

    @Override
    public int fill(MessagePassingQueue.Supplier<E> s) {
        return MessagePassingQueueUtil.fillUnbounded(this, s);
    }

    @Override
    protected int getNextBufferSize(E[] buffer) {
        return LinkedArrayQueueUtil.length(buffer);
    }

    @Override
    protected long getCurrentBufferCapacity(long mask) {
        return mask;
    }
}

