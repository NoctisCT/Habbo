/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.status;

import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.StatusListener;
import java.util.ArrayList;
import java.util.List;

public class StatusListenerAsList
implements StatusListener {
    List<Status> statusList = new ArrayList<Status>();

    @Override
    public void addStatusEvent(Status status) {
        this.statusList.add(status);
    }

    public List<Status> getStatusList() {
        return this.statusList;
    }
}

