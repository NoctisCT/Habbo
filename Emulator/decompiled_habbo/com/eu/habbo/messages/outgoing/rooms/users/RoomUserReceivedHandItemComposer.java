/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class RoomUserReceivedHandItemComposer
extends MessageComposer {
    private RoomUnit from;
    private int handItem;

    public RoomUserReceivedHandItemComposer(RoomUnit from, int handItem) {
        this.from = from;
        this.handItem = handItem;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(354);
        this.response.appendInt(this.from.getId());
        this.response.appendInt(this.handItem);
        return this.response;
    }

    public RoomUnit getFrom() {
        return this.from;
    }

    public int getHandItem() {
        return this.handItem;
    }
}

