/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core;

import ch.qos.logback.core.helpers.CyclicBuffer;
import ch.qos.logback.core.spi.LogbackLock;
import ch.qos.logback.core.status.OnConsoleStatusListener;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.StatusListener;
import ch.qos.logback.core.status.StatusManager;
import java.util.ArrayList;
import java.util.List;

public class BasicStatusManager
implements StatusManager {
    public static final int MAX_HEADER_COUNT = 150;
    public static final int TAIL_SIZE = 150;
    int count = 0;
    protected final List<Status> statusList = new ArrayList<Status>();
    protected final CyclicBuffer<Status> tailBuffer = new CyclicBuffer(150);
    protected final LogbackLock statusListLock = new LogbackLock();
    int level = 0;
    protected final List<StatusListener> statusListenerList = new ArrayList<StatusListener>();
    protected final LogbackLock statusListenerListLock = new LogbackLock();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void add(Status newStatus) {
        this.fireStatusAddEvent(newStatus);
        ++this.count;
        if (newStatus.getLevel() > this.level) {
            this.level = newStatus.getLevel();
        }
        LogbackLock logbackLock = this.statusListLock;
        synchronized (logbackLock) {
            if (this.statusList.size() < 150) {
                this.statusList.add(newStatus);
            } else {
                this.tailBuffer.add(newStatus);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<Status> getCopyOfStatusList() {
        LogbackLock logbackLock = this.statusListLock;
        synchronized (logbackLock) {
            ArrayList<Status> tList = new ArrayList<Status>(this.statusList);
            tList.addAll(this.tailBuffer.asList());
            return tList;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void fireStatusAddEvent(Status status) {
        LogbackLock logbackLock = this.statusListenerListLock;
        synchronized (logbackLock) {
            for (StatusListener sl : this.statusListenerList) {
                sl.addStatusEvent(status);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void clear() {
        LogbackLock logbackLock = this.statusListLock;
        synchronized (logbackLock) {
            this.count = 0;
            this.statusList.clear();
            this.tailBuffer.clear();
        }
    }

    public int getLevel() {
        return this.level;
    }

    @Override
    public int getCount() {
        return this.count;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean add(StatusListener listener) {
        LogbackLock logbackLock = this.statusListenerListLock;
        synchronized (logbackLock) {
            boolean alreadyPresent;
            if (listener instanceof OnConsoleStatusListener && (alreadyPresent = this.checkForPresence(this.statusListenerList, listener.getClass()))) {
                return false;
            }
            this.statusListenerList.add(listener);
        }
        return true;
    }

    private boolean checkForPresence(List<StatusListener> statusListenerList, Class<?> aClass) {
        for (StatusListener e : statusListenerList) {
            if (e.getClass() != aClass) continue;
            return true;
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void remove(StatusListener listener) {
        LogbackLock logbackLock = this.statusListenerListLock;
        synchronized (logbackLock) {
            this.statusListenerList.remove(listener);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<StatusListener> getCopyOfStatusListenerList() {
        LogbackLock logbackLock = this.statusListenerListLock;
        synchronized (logbackLock) {
            return new ArrayList<StatusListener>(this.statusListenerList);
        }
    }
}

