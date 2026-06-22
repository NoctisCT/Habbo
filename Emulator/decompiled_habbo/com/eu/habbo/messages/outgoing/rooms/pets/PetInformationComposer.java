/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms.pets;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.pets.MonsterplantPet;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.PetManager;
import com.eu.habbo.habbohotel.pets.RideablePet;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class PetInformationComposer
extends MessageComposer {
    private final Pet pet;
    private final Room room;
    private final Habbo requestingHabbo;

    public PetInformationComposer(Pet pet, Room room, Habbo requestingHabbo) {
        this.pet = pet;
        this.room = room;
        this.requestingHabbo = requestingHabbo;
    }

    @Override
    protected ServerMessage composeInternal() {
        double days = Math.floor((Emulator.getIntUnixTimestamp() - this.pet.getCreated()) / 86400);
        this.response.init(2901);
        this.response.appendInt(this.pet.getId());
        this.response.appendString(this.pet.getName());
        if (this.pet instanceof MonsterplantPet) {
            this.response.appendInt(((MonsterplantPet)this.pet).getGrowthStage());
            this.response.appendInt(7);
        } else {
            this.response.appendInt(this.pet.getLevel());
            this.response.appendInt(20);
        }
        this.response.appendInt(this.pet.getExperience());
        if (this.pet.getLevel() < PetManager.experiences.length + 1) {
            this.response.appendInt(PetManager.experiences[this.pet.getLevel() - 1]);
        } else {
            this.response.appendInt(this.pet.getExperience());
        }
        this.response.appendInt(this.pet.getEnergy());
        this.response.appendInt(this.pet.getMaxEnergy());
        this.response.appendInt(this.pet.getHappiness());
        this.response.appendInt(100);
        this.response.appendInt(this.pet.getRespect());
        this.response.appendInt(this.pet.getUserId());
        this.response.appendInt((int)days + 1);
        this.response.appendString(this.room.getFurniOwnerName(this.pet.getUserId()));
        this.response.appendInt(this.pet instanceof MonsterplantPet ? ((MonsterplantPet)this.pet).getRarity() : 0);
        this.response.appendBoolean(this.pet instanceof RideablePet && this.requestingHabbo != null && (((RideablePet)this.pet).getRider() == null || this.pet.getUserId() == this.requestingHabbo.getHabboInfo().getId()) && ((RideablePet)this.pet).hasSaddle());
        this.response.appendBoolean(this.pet instanceof RideablePet && ((RideablePet)this.pet).getRider() != null && this.requestingHabbo != null && ((RideablePet)this.pet).getRider().getHabboInfo().getId() == this.requestingHabbo.getHabboInfo().getId());
        this.response.appendInt(0);
        this.response.appendInt(this.pet instanceof RideablePet && ((RideablePet)this.pet).anyoneCanRide() ? 1 : 0);
        this.response.appendBoolean(this.pet instanceof MonsterplantPet && ((MonsterplantPet)this.pet).canBreed());
        this.response.appendBoolean(!(this.pet instanceof MonsterplantPet) || !((MonsterplantPet)this.pet).isFullyGrown());
        this.response.appendBoolean(this.pet instanceof MonsterplantPet && ((MonsterplantPet)this.pet).isDead());
        this.response.appendInt(this.pet instanceof MonsterplantPet ? ((MonsterplantPet)this.pet).getRarity() : 0);
        this.response.appendInt(MonsterplantPet.timeToLive);
        this.response.appendInt(this.pet instanceof MonsterplantPet ? ((MonsterplantPet)this.pet).remainingTimeToLive() : 0);
        this.response.appendInt(this.pet instanceof MonsterplantPet ? ((MonsterplantPet)this.pet).remainingGrowTime() : 0);
        this.response.appendBoolean(this.pet instanceof MonsterplantPet && ((MonsterplantPet)this.pet).isPubliclyBreedable());
        return this.response;
    }

    public Pet getPet() {
        return this.pet;
    }

    public Room getRoom() {
        return this.room;
    }

    public Habbo getRequestingHabbo() {
        return this.requestingHabbo;
    }
}

