/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.events.users.UserEvent;

public class UserRightsGivenEvent
extends UserEvent {
    public final Habbo receiver;

    public UserRightsGivenEvent(Habbo habbo, Habbo receiver) {
        super(habbo);
        this.receiver = receiver;
    }
}

