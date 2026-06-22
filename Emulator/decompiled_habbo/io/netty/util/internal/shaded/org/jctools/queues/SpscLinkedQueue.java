/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.queues.BaseLinkedQueue;
import io.netty.util.internal.shaded.org.jctools.queues.LinkedQueueNode;
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueueUtil;

public class SpscLinkedQueue<E>
extends BaseLinkedQueue<E> {
    public SpscLinkedQueue() {
        LinkedQueueNode node = this.newNode();
        this.spProducerNode(node);
        this.spConsumerNode(node);
        node.soNext(null);
    }

    @Override
    public boolean offer(E e) {
        if (null == e) {
            throw new NullPointerException();
        }
        LinkedQueueNode<E> nextNode = this.newNode(e);
        this.lpProducerNode().soNext(nextNode);
        this.spProducerNode(nextNode);
        return true;
    }

    @Override
    public E poll() {
        return (E)this.relaxedPoll();
    }

    @Override
    public E peek() {
        return (E)this.relaxedPeek();
    }

    @Override
    public int fill(MessagePassingQueue.Supplier<E> s) {
        return MessagePassingQueueUtil.fillUnbounded(this, s);
    }

    @Override
    public int fill(MessagePassingQueue.Supplier<E> s, int limit) {
        LinkedQueueNode<E> tail;
        if (null == s) {
            throw new IllegalArgumentException("supplier is null");
        }
        if (limit < 0) {
            throw new IllegalArgumentException("limit is negative:" + limit);
        }
        if (limit == 0) {
            return 0;
        }
        LinkedQueueNode<E> head = tail = this.newNode(s.get());
        for (int i = 1; i < limit; ++i) {
            LinkedQueueNode<E> temp = this.newNode(s.get());
            tail.soNext(temp);
            tail = temp;
        }
        LinkedQueueNode<E> oldPNode = this.lpProducerNode();
        oldPNode.soNext(head);
        this.spProducerNode(tail);
        return limit;
    }

    @Override
    public void fill(MessagePassingQueue.Supplier<E> s, MessagePassingQueue.WaitStrategy wait, MessagePassingQueue.ExitCondition exit) {
        if (null == wait) {
            throw new IllegalArgumentException("waiter is null");
        }
        if (null == exit) {
            throw new IllegalArgumentException("exit condition is null");
        }
        if (null == s) {
            throw new IllegalArgumentException("supplier is null");
        }
        LinkedQueueNode<E> chaserNode = this.lpProducerNode();
        while (exit.keepRunning()) {
            for (int i = 0; i < 4096; ++i) {
                LinkedQueueNode<E> nextNode = this.newNode(s.get());
                chaserNode.soNext(nextNode);
                chaserNode = nextNode;
                this.spProducerNode(chaserNode);
            }
        }
    }
}

