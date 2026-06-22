/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class RoomUserRemoveComposer
extends MessageComposer {
    private final RoomUnit roomUnit;

    public RoomUserRemoveComposer(RoomUnit roomUnit) {
        this.roomUnit = roomUnit;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2661);
        this.response.appendString("" + this.roomUnit.getId());
        return this.response;
    }

    public RoomUnit getRoomUnit() {
        return this.roomUnit;
    }
}

