/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.events.users.UserEvent;

public class UserPointsEvent
extends UserEvent {
    public int points;
    public int type;

    public UserPointsEvent(Habbo habbo, int points, int type) {
        super(habbo);
        this.points = points;
        this.type = type;
    }
}

