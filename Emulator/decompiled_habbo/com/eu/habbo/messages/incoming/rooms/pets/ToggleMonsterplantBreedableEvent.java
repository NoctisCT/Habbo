/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.incoming.rooms.pets;

import com.eu.habbo.habbohotel.pets.MonsterplantPet;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.messages.incoming.MessageHandler;

public class ToggleMonsterplantBreedableEvent
extends MessageHandler {
    @Override
    public void handle() throws Exception {
        int petId = this.packet.readInt();
        Pet pet = this.client.getHabbo().getHabboInfo().getCurrentRoom().getPet(petId);
        if (pet != null && pet.getUserId() == this.client.getHabbo().getHabboInfo().getId() && pet instanceof MonsterplantPet) {
            ((MonsterplantPet)pet).setPubliclyBreedable(((MonsterplantPet)pet).isPubliclyBreedable());
        }
    }
}

