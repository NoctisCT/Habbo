/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.plugin.events.roomunit;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.plugin.events.roomunit.RoomUnitEvent;

public class RoomUnitSetGoalEvent
extends RoomUnitEvent {
    public final RoomTile goal;

    public RoomUnitSetGoalEvent(Room room, RoomUnit roomUnit, RoomTile goal) {
        super(room, roomUnit);
        this.goal = goal;
    }

    public void setGoal(RoomTile t) {
        this.roomUnit.setGoalLocation(t);
    }
}

