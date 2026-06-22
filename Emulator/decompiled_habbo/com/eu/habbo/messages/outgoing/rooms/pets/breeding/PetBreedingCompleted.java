/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms.pets.breeding;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class PetBreedingCompleted
extends MessageComposer {
    private final int type;
    private final int race;

    public PetBreedingCompleted(int type, int race) {
        this.type = type;
        this.race = race;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2527);
        this.response.appendInt(this.type);
        this.response.appendInt(this.race);
        return this.response;
    }

    public int getType() {
        return this.type;
    }

    public int getRace() {
        return this.race;
    }
}

