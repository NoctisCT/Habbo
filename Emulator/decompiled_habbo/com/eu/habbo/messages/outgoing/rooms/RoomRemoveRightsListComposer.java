/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class RoomRemoveRightsListComposer
extends MessageComposer {
    private final Room room;
    private final int userId;

    public RoomRemoveRightsListComposer(Room room, int userId) {
        this.room = room;
        this.userId = userId;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1327);
        this.response.appendInt(this.room.getId());
        this.response.appendInt(this.userId);
        return this.response;
    }

    public Room getRoom() {
        return this.room;
    }

    public int getUserId() {
        return this.userId;
    }
}

