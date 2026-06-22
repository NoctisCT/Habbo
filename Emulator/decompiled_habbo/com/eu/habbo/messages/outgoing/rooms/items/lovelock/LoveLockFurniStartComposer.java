/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms.items.lovelock;

import com.eu.habbo.habbohotel.items.interactions.InteractionLoveLock;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class LoveLockFurniStartComposer
extends MessageComposer {
    private final InteractionLoveLock loveLock;

    public LoveLockFurniStartComposer(InteractionLoveLock loveLock) {
        this.loveLock = loveLock;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(3753);
        this.response.appendInt(this.loveLock.getId());
        this.response.appendBoolean(true);
        return this.response;
    }

    public InteractionLoveLock getLoveLock() {
        return this.loveLock;
    }
}

