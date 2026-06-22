/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.habbohotel.rooms.RoomRightLevels;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class RoomRightsComposer
extends MessageComposer {
    private final RoomRightLevels type;

    public RoomRightsComposer(RoomRightLevels type) {
        this.type = type;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(780);
        this.response.appendInt(this.type.level);
        return this.response;
    }

    public RoomRightLevels getType() {
        return this.type;
    }
}

