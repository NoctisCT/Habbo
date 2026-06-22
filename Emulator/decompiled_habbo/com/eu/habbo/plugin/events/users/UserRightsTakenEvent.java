/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.events.users.UserEvent;

public class UserRightsTakenEvent
extends UserEvent {
    public final int victimId;
    public final Habbo victim;

    public UserRightsTakenEvent(Habbo habbo, int victimId, Habbo victim) {
        super(habbo);
        this.victimId = victimId;
        this.victim = victim;
    }
}

