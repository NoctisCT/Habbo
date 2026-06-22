/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class RoomSettingsUpdatedComposer
extends MessageComposer {
    private final Room room;

    public RoomSettingsUpdatedComposer(Room room) {
        this.room = room;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(3297);
        this.response.appendInt(this.room.getId());
        return this.response;
    }

    public Room getRoom() {
        return this.room;
    }
}

