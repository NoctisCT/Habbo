/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class RoomEntryInfoComposer
extends MessageComposer {
    private final Room room;

    public RoomEntryInfoComposer(Room room) {
        this.room = room;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(-1);
        this.response.appendInt(this.room.getId());
        this.response.appendString(this.room.getOwnerName());
        return this.response;
    }

    public Room getRoom() {
        return this.room;
    }
}

