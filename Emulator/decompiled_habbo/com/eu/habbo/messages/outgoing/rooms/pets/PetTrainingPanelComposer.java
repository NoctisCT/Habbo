/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms.pets;

import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.PetCommand;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import java.util.ArrayList;
import java.util.Collections;

public class PetTrainingPanelComposer
extends MessageComposer {
    private final Pet pet;

    public PetTrainingPanelComposer(Pet pet) {
        this.pet = pet;
    }

    @Override
    protected ServerMessage composeInternal() {
        ArrayList<PetCommand> enabled = new ArrayList<PetCommand>();
        Collections.sort(this.pet.getPetData().getPetCommands());
        this.response.init(1164);
        this.response.appendInt(this.pet.getId());
        this.response.appendInt(this.pet.getPetData().getPetCommands().size());
        for (PetCommand petCommand : this.pet.getPetData().getPetCommands()) {
            this.response.appendInt(petCommand.id);
            if (this.pet.getLevel() < petCommand.level) continue;
            enabled.add(petCommand);
        }
        if (!enabled.isEmpty()) {
            Collections.sort(enabled);
        }
        this.response.appendInt(enabled.size());
        for (PetCommand petCommand : enabled) {
            this.response.appendInt(petCommand.id);
        }
        return this.response;
    }

    public Pet getPet() {
        return this.pet;
    }
}

