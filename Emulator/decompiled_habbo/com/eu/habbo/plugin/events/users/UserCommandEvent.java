/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.events.users.UserEvent;

public class UserCommandEvent
extends UserEvent {
    public final String[] args;
    public final boolean succes;

    public UserCommandEvent(Habbo habbo, String[] args, boolean succes) {
        super(habbo);
        this.args = args;
        this.succes = succes;
    }
}

