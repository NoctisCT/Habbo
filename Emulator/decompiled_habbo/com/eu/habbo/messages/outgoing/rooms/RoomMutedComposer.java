/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class RoomMutedComposer
extends MessageComposer {
    private final Room room;

    public RoomMutedComposer(Room room) {
        this.room = room;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2533);
        this.response.appendBoolean(this.room.isMuted());
        return this.response;
    }

    public Room getRoom() {
        return this.room;
    }
}

