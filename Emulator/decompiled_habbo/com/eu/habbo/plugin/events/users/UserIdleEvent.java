/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.events.users.UserEvent;

public class UserIdleEvent
extends UserEvent {
    public final IdleReason reason;
    public boolean idle;

    public UserIdleEvent(Habbo habbo, IdleReason reason, boolean idle) {
        super(habbo);
        this.reason = reason;
        this.idle = idle;
    }

    public static enum IdleReason {
        ACTION,
        DANCE,
        TIMEOUT,
        WALKED,
        TALKED;

    }
}

