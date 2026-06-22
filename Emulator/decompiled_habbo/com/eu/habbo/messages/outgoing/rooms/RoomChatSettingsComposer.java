/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class RoomChatSettingsComposer
extends MessageComposer {
    private final Room room;

    public RoomChatSettingsComposer(Room room) {
        this.room = room;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1191);
        this.response.appendInt(this.room.getChatMode());
        this.response.appendInt(this.room.getChatWeight());
        this.response.appendInt(this.room.getChatSpeed());
        this.response.appendInt(this.room.getChatDistance());
        this.response.appendInt(this.room.getChatProtection());
        return this.response;
    }

    public Room getRoom() {
        return this.room;
    }
}

