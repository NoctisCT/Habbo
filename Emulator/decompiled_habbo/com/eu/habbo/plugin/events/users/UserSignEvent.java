/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.events.users.UserEvent;

public class UserSignEvent
extends UserEvent {
    public int sign;

    public UserSignEvent(Habbo habbo, int sign) {
        super(habbo);
        this.sign = sign;
    }
}

