/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.inventory;

import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class RemovePetComposer
extends MessageComposer {
    private final int petId;

    public RemovePetComposer(Pet pet) {
        this.petId = pet.getId();
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(3253);
        this.response.appendInt(this.petId);
        return this.response;
    }

    public int getPetId() {
        return this.petId;
    }
}

