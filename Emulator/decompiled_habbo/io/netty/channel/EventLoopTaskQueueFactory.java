/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel;

import java.util.Queue;

public interface EventLoopTaskQueueFactory {
    public Queue<Runnable> newTaskQueue(int var1);
}

