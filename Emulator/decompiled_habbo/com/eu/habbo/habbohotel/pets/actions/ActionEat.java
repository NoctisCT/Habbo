/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.pets.actions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.PetAction;
import com.eu.habbo.habbohotel.pets.PetVocalsType;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.threading.runnables.PetClearPosture;

public class ActionEat
extends PetAction {
    public ActionEat() {
        super(null, true);
        this.statusToSet.add(RoomUnitStatus.EAT);
    }

    @Override
    public boolean apply(Pet pet, Habbo habbo, String[] data) {
        if (pet.getLevelHunger() > 40) {
            pet.say(pet.getPetData().randomVocal(PetVocalsType.HUNGRY));
            Emulator.getThreading().run(new PetClearPosture(pet, RoomUnitStatus.EAT, null, false), 500L);
            pet.eat();
            return true;
        }
        pet.say(pet.getPetData().randomVocal(PetVocalsType.DISOBEY));
        return false;
    }
}

