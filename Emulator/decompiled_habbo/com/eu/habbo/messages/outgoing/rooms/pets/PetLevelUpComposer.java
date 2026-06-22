/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms.pets;

import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class PetLevelUpComposer
extends MessageComposer {
    private final Pet pet;

    public PetLevelUpComposer(Pet pet) {
        this.pet = pet;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(859);
        this.response.appendInt(this.pet.getId());
        this.response.appendString(this.pet.getName());
        this.response.appendInt(this.pet.getLevel());
        this.response.appendInt(this.pet.getPetData().getType());
        this.response.appendInt(this.pet.getRace());
        this.response.appendString(this.pet.getColor());
        this.response.appendInt(0);
        this.response.appendInt(0);
        return this.response;
    }

    public Pet getPet() {
        return this.pet;
    }
}

