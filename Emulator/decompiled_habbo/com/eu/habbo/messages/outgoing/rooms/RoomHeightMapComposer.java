/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class RoomHeightMapComposer
extends MessageComposer {
    private final Room room;

    public RoomHeightMapComposer(Room room) {
        this.room = room;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1301);
        this.response.appendBoolean(true);
        this.response.appendInt(this.room.getWallHeight());
        this.response.appendString(this.room.getLayout().getRelativeMap());
        return this.response;
    }

    public Room getRoom() {
        return this.room;
    }
}

