/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.events.users.UserEvent;

public class UserRespectedEvent
extends UserEvent {
    public final Habbo from;

    public UserRespectedEvent(Habbo habbo, Habbo from) {
        super(habbo);
        this.from = from;
    }
}

