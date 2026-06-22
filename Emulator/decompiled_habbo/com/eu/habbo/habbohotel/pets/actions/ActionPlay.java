/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.pets.actions;

import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.PetAction;
import com.eu.habbo.habbohotel.pets.PetVocalsType;
import com.eu.habbo.habbohotel.users.Habbo;

public class ActionPlay
extends PetAction {
    public ActionPlay() {
        super(null, false);
    }

    @Override
    public boolean apply(Pet pet, Habbo habbo, String[] data) {
        if (pet.getHappiness() > 75) {
            pet.say(pet.getPetData().randomVocal(PetVocalsType.PLAYFUL));
        } else {
            pet.say(pet.getPetData().randomVocal(PetVocalsType.DISOBEY));
        }
        return true;
    }
}

