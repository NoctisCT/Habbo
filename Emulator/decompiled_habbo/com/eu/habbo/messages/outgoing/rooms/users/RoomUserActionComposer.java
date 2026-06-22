/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.rooms.RoomUserAction;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class RoomUserActionComposer
extends MessageComposer {
    private RoomUserAction action;
    private RoomUnit roomUnit;

    public RoomUserActionComposer(RoomUnit roomUnit, RoomUserAction action) {
        this.roomUnit = roomUnit;
        this.action = action;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1631);
        this.response.appendInt(this.roomUnit.getId());
        this.response.appendInt(this.action.getAction());
        return this.response;
    }

    public RoomUserAction getAction() {
        return this.action;
    }

    public RoomUnit getRoomUnit() {
        return this.roomUnit;
    }
}

