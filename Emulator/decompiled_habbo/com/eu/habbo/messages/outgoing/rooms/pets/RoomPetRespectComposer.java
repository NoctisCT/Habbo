/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms.pets;

import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class RoomPetRespectComposer
extends MessageComposer {
    public static final int PET_RESPECTED = 1;
    public static final int PET_TREATED = 2;
    private final Pet pet;
    private final int type;

    public RoomPetRespectComposer(Pet pet) {
        this.pet = pet;
        this.type = 1;
    }

    public RoomPetRespectComposer(Pet pet, int type) {
        this.pet = pet;
        this.type = type;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2788);
        this.response.appendInt(this.type);
        this.response.appendInt(100);
        this.pet.serialize(this.response);
        return this.response;
    }

    public Pet getPet() {
        return this.pet;
    }

    public int getType() {
        return this.type;
    }
}

