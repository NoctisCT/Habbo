/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.AbstractScheduledEventExecutor;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.PromiseTask;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.internal.DefaultPriorityQueue;
import io.netty.util.internal.PriorityQueueNode;
import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

final class ScheduledFutureTask<V>
extends PromiseTask<V>
implements ScheduledFuture<V>,
PriorityQueueNode {
    private static final long START_TIME = System.nanoTime();
    private long id;
    private long deadlineNanos;
    private final long periodNanos;
    private int queueIndex = -1;

    static long nanoTime() {
        return System.nanoTime() - START_TIME;
    }

    static long deadlineNanos(long delay) {
        long deadlineNanos = ScheduledFutureTask.nanoTime() + delay;
        return deadlineNanos < 0L ? Long.MAX_VALUE : deadlineNanos;
    }

    static long initialNanoTime() {
        return START_TIME;
    }

    ScheduledFutureTask(AbstractScheduledEventExecutor executor, Runnable runnable, long nanoTime) {
        super((EventExecutor)executor, runnable);
        this.deadlineNanos = nanoTime;
        this.periodNanos = 0L;
    }

    ScheduledFutureTask(AbstractScheduledEventExecutor executor, Runnable runnable, long nanoTime, long period) {
        super((EventExecutor)executor, runnable);
        this.deadlineNanos = nanoTime;
        this.periodNanos = ScheduledFutureTask.validatePeriod(period);
    }

    ScheduledFutureTask(AbstractScheduledEventExecutor executor, Callable<V> callable, long nanoTime, long period) {
        super((EventExecutor)executor, callable);
        this.deadlineNanos = nanoTime;
        this.periodNanos = ScheduledFutureTask.validatePeriod(period);
    }

    ScheduledFutureTask(AbstractScheduledEventExecutor executor, Callable<V> callable, long nanoTime) {
        super((EventExecutor)executor, callable);
        this.deadlineNanos = nanoTime;
        this.periodNanos = 0L;
    }

    private static long validatePeriod(long period) {
        if (period == 0L) {
            throw new IllegalArgumentException("period: 0 (expected: != 0)");
        }
        return period;
    }

    ScheduledFutureTask<V> setId(long id) {
        if (this.id == 0L) {
            this.id = id;
        }
        return this;
    }

    @Override
    protected EventExecutor executor() {
        return super.executor();
    }

    public long deadlineNanos() {
        return this.deadlineNanos;
    }

    void setConsumed() {
        if (this.periodNanos == 0L) {
            assert (ScheduledFutureTask.nanoTime() >= this.deadlineNanos);
            this.deadlineNanos = 0L;
        }
    }

    public long delayNanos() {
        return ScheduledFutureTask.deadlineToDelayNanos(this.deadlineNanos());
    }

    static long deadlineToDelayNanos(long deadlineNanos) {
        return deadlineNanos == 0L ? 0L : Math.max(0L, deadlineNanos - ScheduledFutureTask.nanoTime());
    }

    public long delayNanos(long currentTimeNanos) {
        return this.deadlineNanos == 0L ? 0L : Math.max(0L, this.deadlineNanos() - (currentTimeNanos - START_TIME));
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(this.delayNanos(), TimeUnit.NANOSECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        if (this == o) {
            return 0;
        }
        ScheduledFutureTask that = (ScheduledFutureTask)o;
        long d = this.deadlineNanos() - that.deadlineNanos();
        if (d < 0L) {
            return -1;
        }
        if (d > 0L) {
            return 1;
        }
        if (this.id < that.id) {
            return -1;
        }
        assert (this.id != that.id);
        return 1;
    }

    @Override
    public void run() {
        assert (this.executor().inEventLoop());
        try {
            if (this.delayNanos() > 0L) {
                if (this.isCancelled()) {
                    this.scheduledExecutor().scheduledTaskQueue().removeTyped(this);
                } else {
                    this.scheduledExecutor().scheduleFromEventLoop(this);
                }
                return;
            }
            if (this.periodNanos == 0L) {
                if (this.setUncancellableInternal()) {
                    Object result = this.runTask();
                    this.setSuccessInternal(result);
                }
            } else if (!this.isCancelled()) {
                this.runTask();
                if (!this.executor().isShutdown()) {
                    this.deadlineNanos = this.periodNanos > 0L ? (this.deadlineNanos += this.periodNanos) : ScheduledFutureTask.nanoTime() - this.periodNanos;
                    if (!this.isCancelled()) {
                        this.scheduledExecutor().scheduledTaskQueue().add(this);
                    }
                }
            }
        }
        catch (Throwable cause) {
            this.setFailureInternal(cause);
        }
    }

    private AbstractScheduledEventExecutor scheduledExecutor() {
        return (AbstractScheduledEventExecutor)this.executor();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        boolean canceled = super.cancel(mayInterruptIfRunning);
        if (canceled) {
            this.scheduledExecutor().removeScheduled(this);
        }
        return canceled;
    }

    boolean cancelWithoutRemove(boolean mayInterruptIfRunning) {
        return super.cancel(mayInterruptIfRunning);
    }

    @Override
    protected StringBuilder toStringBuilder() {
        StringBuilder buf = super.toStringBuilder();
        buf.setCharAt(buf.length() - 1, ',');
        return buf.append(" deadline: ").append(this.deadlineNanos).append(", period: ").append(this.periodNanos).append(')');
    }

    @Override
    public int priorityQueueIndex(DefaultPriorityQueue<?> queue) {
        return this.queueIndex;
    }

    @Override
    public void priorityQueueIndex(DefaultPriorityQueue<?> queue, int i) {
        this.queueIndex = i;
    }
}

