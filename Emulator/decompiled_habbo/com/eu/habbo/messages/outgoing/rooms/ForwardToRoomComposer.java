/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class ForwardToRoomComposer
extends MessageComposer {
    private final int roomId;

    public ForwardToRoomComposer(int roomId) {
        this.roomId = roomId;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(160);
        this.response.appendInt(this.roomId);
        return this.response;
    }

    public int getRoomId() {
        return this.roomId;
    }
}

