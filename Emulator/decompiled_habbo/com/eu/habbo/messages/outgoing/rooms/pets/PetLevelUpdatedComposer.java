/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms.pets;

import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class PetLevelUpdatedComposer
extends MessageComposer {
    private final Pet pet;

    public PetLevelUpdatedComposer(Pet pet) {
        this.pet = pet;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2824);
        this.response.appendInt(this.pet.getRoomUnit().getId());
        this.response.appendInt(this.pet.getId());
        this.response.appendInt(this.pet.getLevel());
        return this.response;
    }

    public Pet getPet() {
        return this.pet;
    }
}

