/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.wired;

import com.eu.habbo.habbohotel.items.interactions.InteractionWiredCondition;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class WiredConditionDataComposer
extends MessageComposer {
    private final InteractionWiredCondition condition;
    private final Room room;

    public WiredConditionDataComposer(InteractionWiredCondition condition, Room room) {
        this.condition = condition;
        this.room = room;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1108);
        this.condition.serializeWiredData(this.response, this.room);
        this.condition.needsUpdate(true);
        return this.response;
    }

    public InteractionWiredCondition getCondition() {
        return this.condition;
    }

    public Room getRoom() {
        return this.room;
    }
}

