/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.events.users.UserEvent;

public class UserGetIPAddressEvent
extends UserEvent {
    public final String oldIp;
    private String updatedIp;
    private boolean changedIP = false;

    public UserGetIPAddressEvent(Habbo habbo, String ip) {
        super(habbo);
        this.oldIp = ip;
    }

    public void setUpdatedIp(String updatedIp) {
        this.updatedIp = updatedIp;
        this.changedIP = true;
    }

    public boolean hasChangedIP() {
        return this.changedIP;
    }

    public String getUpdatedIp() {
        return this.updatedIp;
    }
}

