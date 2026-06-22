/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.wired;

import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class WiredEffectDataComposer
extends MessageComposer {
    private final InteractionWiredEffect effect;
    private final Room room;

    public WiredEffectDataComposer(InteractionWiredEffect effect, Room room) {
        this.effect = effect;
        this.room = room;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1434);
        this.effect.serializeWiredData(this.response, this.room);
        this.effect.needsUpdate(true);
        return this.response;
    }

    public InteractionWiredEffect getEffect() {
        return this.effect;
    }

    public Room getRoom() {
        return this.room;
    }
}

