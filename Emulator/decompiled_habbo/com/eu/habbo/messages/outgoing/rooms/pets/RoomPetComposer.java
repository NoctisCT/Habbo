/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms.pets;

import com.eu.habbo.habbohotel.pets.HorsePet;
import com.eu.habbo.habbohotel.pets.IPetLook;
import com.eu.habbo.habbohotel.pets.MonsterplantPet;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.RideablePet;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TIntObjectProcedure;

public class RoomPetComposer
extends MessageComposer
implements TIntObjectProcedure<Pet> {
    private final TIntObjectMap<Pet> pets;

    public RoomPetComposer(Pet pet) {
        this.pets = new TIntObjectHashMap<Pet>();
        this.pets.put(pet.getId(), pet);
    }

    public RoomPetComposer(TIntObjectMap<Pet> pets) {
        this.pets = pets;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(374);
        this.response.appendInt(this.pets.size());
        this.pets.forEachEntry(this);
        return this.response;
    }

    @Override
    public boolean execute(int a, Pet pet) {
        this.response.appendInt(pet.getId());
        this.response.appendString(pet.getName());
        this.response.appendString("");
        if (pet instanceof IPetLook) {
            this.response.appendString(((IPetLook)((Object)pet)).getLook());
        } else {
            this.response.appendString(pet.getPetData().getType() + " " + pet.getRace() + " " + pet.getColor() + " " + (String)(pet instanceof HorsePet ? (((HorsePet)pet).hasSaddle() ? "3" : "2") + " 2 " + ((HorsePet)pet).getHairStyle() + " " + ((HorsePet)pet).getHairColor() + " 3 " + ((HorsePet)pet).getHairStyle() + " " + ((HorsePet)pet).getHairColor() + (((HorsePet)pet).hasSaddle() ? " 4 9 0" : "") : (pet instanceof MonsterplantPet ? (((MonsterplantPet)pet).look.isEmpty() ? "2 1 8 6 0 -1 -1" : ((MonsterplantPet)pet).look) : "2 2 -1 0 3 -1 0")));
        }
        this.response.appendInt(pet.getRoomUnit().getId());
        this.response.appendInt(pet.getRoomUnit().getX());
        this.response.appendInt(pet.getRoomUnit().getY());
        this.response.appendString("" + pet.getRoomUnit().getZ());
        this.response.appendInt(0);
        this.response.appendInt(2);
        this.response.appendInt(pet.getPetData().getType());
        this.response.appendInt(pet.getUserId());
        this.response.appendString(pet.getRoom().getFurniOwnerNames().get(pet.getUserId()));
        this.response.appendInt(pet instanceof MonsterplantPet ? ((MonsterplantPet)pet).getRarity() : 1);
        this.response.appendBoolean(pet instanceof RideablePet && ((RideablePet)pet).hasSaddle());
        this.response.appendBoolean(false);
        this.response.appendBoolean(pet instanceof MonsterplantPet && ((MonsterplantPet)pet).canBreed());
        this.response.appendBoolean(!(pet instanceof MonsterplantPet) || !((MonsterplantPet)pet).isFullyGrown());
        this.response.appendBoolean(pet instanceof MonsterplantPet && ((MonsterplantPet)pet).isDead());
        this.response.appendBoolean(pet instanceof MonsterplantPet && ((MonsterplantPet)pet).isPubliclyBreedable());
        this.response.appendInt(pet instanceof MonsterplantPet ? ((MonsterplantPet)pet).getGrowthStage() : pet.getLevel());
        this.response.appendString("");
        return true;
    }

    public TIntObjectMap<Pet> getPets() {
        return this.pets;
    }
}

