/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.guides;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class GuideSessionInvitedToGuideRoomComposer
extends MessageComposer {
    private final Room room;

    public GuideSessionInvitedToGuideRoomComposer(Room room) {
        this.room = room;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(219);
        this.response.appendInt(this.room != null ? this.room.getId() : 0);
        this.response.appendString(this.room != null ? this.room.getName() : "");
        return this.response;
    }

    public Room getRoom() {
        return this.room;
    }
}

