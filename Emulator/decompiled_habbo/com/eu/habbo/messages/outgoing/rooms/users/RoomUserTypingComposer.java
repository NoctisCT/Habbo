/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class RoomUserTypingComposer
extends MessageComposer {
    private final RoomUnit roomUnit;
    private final boolean typing;

    public RoomUserTypingComposer(RoomUnit roomUnit, boolean typing) {
        this.roomUnit = roomUnit;
        this.typing = typing;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1717);
        this.response.appendInt(this.roomUnit.getId());
        this.response.appendInt(this.typing ? 1 : 0);
        return this.response;
    }

    public RoomUnit getRoomUnit() {
        return this.roomUnit;
    }

    public boolean isTyping() {
        return this.typing;
    }
}

