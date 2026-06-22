/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.plugin.events.users;

import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.plugin.events.users.UserEvent;

public class UserRolledEvent
extends UserEvent {
    public final HabboItem roller;
    public final RoomTile location;

    public UserRolledEvent(Habbo habbo, HabboItem roller, RoomTile location) {
        super(habbo);
        this.roller = roller;
        this.location = location;
    }
}

