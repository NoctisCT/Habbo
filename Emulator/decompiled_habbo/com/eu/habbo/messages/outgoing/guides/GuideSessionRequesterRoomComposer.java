/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.guides;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class GuideSessionRequesterRoomComposer
extends MessageComposer {
    private final Room room;

    public GuideSessionRequesterRoomComposer(Room room) {
        this.room = room;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1847);
        this.response.appendInt(this.room != null ? this.room.getId() : 0);
        return this.response;
    }

    public Room getRoom() {
        return this.room;
    }
}

