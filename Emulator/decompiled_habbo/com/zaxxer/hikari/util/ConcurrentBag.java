/*
 * Decompiled with CFR 0.152.
 */
package com.zaxxer.hikari.util;

import com.zaxxer.hikari.util.ClockSource;
import com.zaxxer.hikari.util.FastList;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConcurrentBag<T extends IConcurrentBagEntry>
implements AutoCloseable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConcurrentBag.class);
    private final CopyOnWriteArrayList<T> sharedList;
    private final boolean weakThreadLocals;
    private final ThreadLocal<List<Object>> threadList;
    private final IBagStateListener listener;
    private final AtomicInteger waiters;
    private volatile boolean closed;
    private final SynchronousQueue<T> handoffQueue;

    public ConcurrentBag(IBagStateListener listener) {
        this.listener = listener;
        this.weakThreadLocals = this.useWeakThreadLocals();
        this.handoffQueue = new SynchronousQueue(true);
        this.waiters = new AtomicInteger();
        this.sharedList = new CopyOnWriteArrayList();
        this.threadList = this.weakThreadLocals ? ThreadLocal.withInitial(() -> new ArrayList(16)) : ThreadLocal.withInitial(() -> new FastList(IConcurrentBagEntry.class, 16));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public T borrow(long timeout, TimeUnit timeUnit) throws InterruptedException {
        List<Object> list = this.threadList.get();
        for (int i = list.size() - 1; i >= 0; --i) {
            IConcurrentBagEntry bagEntry;
            Iterator<T> entry = list.remove(i);
            IConcurrentBagEntry iConcurrentBagEntry = bagEntry = this.weakThreadLocals ? (IConcurrentBagEntry)((WeakReference)((Object)entry)).get() : (IConcurrentBagEntry)((Object)entry);
            if (bagEntry == null || !bagEntry.compareAndSet(0, 1)) continue;
            return (T)bagEntry;
        }
        int waiting = this.waiters.incrementAndGet();
        try {
            long start;
            for (IConcurrentBagEntry bagEntry : this.sharedList) {
                if (!bagEntry.compareAndSet(0, 1)) continue;
                if (waiting > 1) {
                    this.listener.addBagItem(waiting - 1);
                }
                IConcurrentBagEntry iConcurrentBagEntry = bagEntry;
                return (T)iConcurrentBagEntry;
            }
            this.listener.addBagItem(waiting);
            timeout = timeUnit.toNanos(timeout);
            do {
                start = ClockSource.currentTime();
                IConcurrentBagEntry bagEntry = (IConcurrentBagEntry)this.handoffQueue.poll(timeout, TimeUnit.NANOSECONDS);
                if (bagEntry != null && !bagEntry.compareAndSet(0, 1)) continue;
                IConcurrentBagEntry iConcurrentBagEntry = bagEntry;
                return (T)iConcurrentBagEntry;
            } while ((timeout -= ClockSource.elapsedNanos(start)) > 10000L);
            T t = null;
            return t;
        }
        finally {
            this.waiters.decrementAndGet();
        }
    }

    public void requite(T bagEntry) {
        bagEntry.setState(0);
        int i = 0;
        while (this.waiters.get() > 0) {
            if (bagEntry.getState() != 0 || this.handoffQueue.offer(bagEntry)) {
                return;
            }
            if ((i & 0xFF) == 255) {
                LockSupport.parkNanos(TimeUnit.MICROSECONDS.toNanos(10L));
            } else {
                Thread.yield();
            }
            ++i;
        }
        List<Object> threadLocalList = this.threadList.get();
        if (threadLocalList.size() < 50) {
            threadLocalList.add(this.weakThreadLocals ? new WeakReference<T>(bagEntry) : bagEntry);
        }
    }

    public void add(T bagEntry) {
        if (this.closed) {
            LOGGER.info("ConcurrentBag has been closed, ignoring add()");
            throw new IllegalStateException("ConcurrentBag has been closed, ignoring add()");
        }
        this.sharedList.add(bagEntry);
        while (this.waiters.get() > 0 && bagEntry.getState() == 0 && !this.handoffQueue.offer(bagEntry)) {
            Thread.yield();
        }
    }

    public boolean remove(T bagEntry) {
        if (!(bagEntry.compareAndSet(1, -1) || bagEntry.compareAndSet(-2, -1) || this.closed)) {
            LOGGER.warn("Attempt to remove an object from the bag that was not borrowed or reserved: {}", (Object)bagEntry);
            return false;
        }
        boolean removed = this.sharedList.remove(bagEntry);
        if (!removed && !this.closed) {
            LOGGER.warn("Attempt to remove an object from the bag that does not exist: {}", (Object)bagEntry);
        }
        this.threadList.get().remove(bagEntry);
        return removed;
    }

    @Override
    public void close() {
        this.closed = true;
    }

    public List<T> values(int state) {
        List list = this.sharedList.stream().filter(e -> e.getState() == state).collect(Collectors.toList());
        Collections.reverse(list);
        return list;
    }

    public List<T> values() {
        return (List)this.sharedList.clone();
    }

    public boolean reserve(T bagEntry) {
        return bagEntry.compareAndSet(0, -2);
    }

    public void unreserve(T bagEntry) {
        if (bagEntry.compareAndSet(-2, 0)) {
            while (this.waiters.get() > 0 && !this.handoffQueue.offer(bagEntry)) {
                Thread.yield();
            }
        } else {
            LOGGER.warn("Attempt to relinquish an object to the bag that was not reserved: {}", (Object)bagEntry);
        }
    }

    public int getWaitingThreadCount() {
        return this.waiters.get();
    }

    public int getCount(int state) {
        int count = 0;
        for (IConcurrentBagEntry e : this.sharedList) {
            if (e.getState() != state) continue;
            ++count;
        }
        return count;
    }

    public int[] getStateCounts() {
        int[] states = new int[6];
        for (IConcurrentBagEntry e : this.sharedList) {
            int n = e.getState();
            states[n] = states[n] + 1;
        }
        states[4] = this.sharedList.size();
        states[5] = this.waiters.get();
        return states;
    }

    public int size() {
        return this.sharedList.size();
    }

    public void dumpState() {
        this.sharedList.forEach(entry -> LOGGER.info(entry.toString()));
    }

    private boolean useWeakThreadLocals() {
        try {
            if (System.getProperty("com.zaxxer.hikari.useWeakReferences") != null) {
                return Boolean.getBoolean("com.zaxxer.hikari.useWeakReferences");
            }
            return this.getClass().getClassLoader() != ClassLoader.getSystemClassLoader();
        }
        catch (SecurityException se) {
            return true;
        }
    }

    public static interface IBagStateListener {
        public void addBagItem(int var1);
    }

    public static interface IConcurrentBagEntry {
        public static final int STATE_NOT_IN_USE = 0;
        public static final int STATE_IN_USE = 1;
        public static final int STATE_REMOVED = -1;
        public static final int STATE_RESERVED = -2;

        public boolean compareAndSet(int var1, int var2);

        public void setState(int var1);

        public int getState();
    }
}

