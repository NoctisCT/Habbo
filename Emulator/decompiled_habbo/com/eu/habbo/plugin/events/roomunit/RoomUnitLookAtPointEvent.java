/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.plugin.events.roomunit;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.plugin.events.roomunit.RoomUnitEvent;

public class RoomUnitLookAtPointEvent
extends RoomUnitEvent {
    public final RoomTile location;

    public RoomUnitLookAtPointEvent(Room room, RoomUnit roomUnit, RoomTile location) {
        super(room, roomUnit);
        this.location = location;
    }
}

