/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms.pets;

import com.eu.habbo.habbohotel.pets.MonsterplantPet;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.RideablePet;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class PetStatusUpdateComposer
extends MessageComposer {
    private final Pet pet;

    public PetStatusUpdateComposer(Pet pet) {
        this.pet = pet;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1907);
        this.response.appendInt(this.pet.getRoomUnit().getId());
        this.response.appendInt(this.pet instanceof RideablePet && ((RideablePet)this.pet).anyoneCanRide() ? 1 : 0);
        this.response.appendBoolean(this.pet instanceof MonsterplantPet && ((MonsterplantPet)this.pet).canBreed());
        this.response.appendBoolean(this.pet instanceof MonsterplantPet && !((MonsterplantPet)this.pet).isFullyGrown());
        this.response.appendBoolean(this.pet instanceof MonsterplantPet && ((MonsterplantPet)this.pet).isDead());
        this.response.appendBoolean(this.pet instanceof MonsterplantPet && ((MonsterplantPet)this.pet).isPubliclyBreedable());
        return this.response;
    }

    public Pet getPet() {
        return this.pet;
    }
}

