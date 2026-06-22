/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class RoomModelComposer
extends MessageComposer {
    private final Room room;

    public RoomModelComposer(Room room) {
        this.room = room;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2031);
        this.response.appendString(this.room.getLayout().getName());
        this.response.appendInt(this.room.getId());
        return this.response;
    }

    public Room getRoom() {
        return this.room;
    }
}

