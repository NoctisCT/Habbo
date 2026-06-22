/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class RoomFloorThicknessUpdatedComposer
extends MessageComposer {
    private final Room room;

    public RoomFloorThicknessUpdatedComposer(Room room) {
        this.room = room;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(3786);
        this.response.appendBoolean(this.room.isHideWall());
        this.response.appendInt(this.room.getFloorSize());
        this.response.appendInt(this.room.getWallSize());
        return this.response;
    }

    public Room getRoom() {
        return this.room;
    }
}

