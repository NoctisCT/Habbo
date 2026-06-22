/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.events.users.UserEvent;

public class UserLoginEvent
extends UserEvent {
    public final String ip;

    public UserLoginEvent(Habbo habbo, String ip) {
        super(habbo);
        this.ip = ip;
    }
}

