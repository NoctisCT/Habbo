/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class RoomPaneComposer
extends MessageComposer {
    private final Room room;
    private final boolean roomOwner;

    public RoomPaneComposer(Room room, boolean roomOwner) {
        this.room = room;
        this.roomOwner = roomOwner;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(749);
        this.response.appendInt(this.room.getId());
        this.response.appendBoolean(this.roomOwner);
        return this.response;
    }

    public Room getRoom() {
        return this.room;
    }

    public boolean isRoomOwner() {
        return this.roomOwner;
    }
}

