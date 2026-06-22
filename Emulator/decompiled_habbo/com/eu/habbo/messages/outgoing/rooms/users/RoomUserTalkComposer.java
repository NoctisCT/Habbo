/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class RoomUserTalkComposer
extends MessageComposer {
    private final RoomChatMessage roomChatMessage;

    public RoomUserTalkComposer(RoomChatMessage roomChatMessage) {
        this.roomChatMessage = roomChatMessage;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1446);
        if (this.roomChatMessage.getMessage().isEmpty()) {
            return null;
        }
        this.roomChatMessage.serialize(this.response);
        return this.response;
    }

    public RoomChatMessage getRoomChatMessage() {
        return this.roomChatMessage;
    }
}

