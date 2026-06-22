/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class RoomUserHandItemComposer
extends MessageComposer {
    private final RoomUnit roomUnit;

    public RoomUserHandItemComposer(RoomUnit roomUnit) {
        this.roomUnit = roomUnit;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1474);
        this.response.appendInt(this.roomUnit.getId());
        this.response.appendInt(this.roomUnit.getHandItem());
        return this.response;
    }

    public RoomUnit getRoomUnit() {
        return this.roomUnit;
    }
}

