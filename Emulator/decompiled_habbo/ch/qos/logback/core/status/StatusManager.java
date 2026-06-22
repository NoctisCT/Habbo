/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.status;

import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.StatusListener;
import java.util.List;

public interface StatusManager {
    public void add(Status var1);

    public List<Status> getCopyOfStatusList();

    public int getCount();

    public boolean add(StatusListener var1);

    public void remove(StatusListener var1);

    public void clear();

    public List<StatusListener> getCopyOfStatusListenerList();
}

