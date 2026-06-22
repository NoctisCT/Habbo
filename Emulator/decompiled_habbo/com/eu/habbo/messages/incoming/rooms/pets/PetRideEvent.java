/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.incoming.rooms.pets;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.RideablePet;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.threading.runnables.RoomUnitRidePet;
import java.util.List;

public class PetRideEvent
extends MessageHandler {
    @Override
    public void handle() throws Exception {
        int petId = this.packet.readInt();
        Habbo habbo = this.client.getHabbo();
        Room room = habbo.getHabboInfo().getCurrentRoom();
        if (room == null) {
            return;
        }
        Pet pet = room.getPet(petId);
        if (!(pet instanceof RideablePet)) {
            return;
        }
        RideablePet rideablePet = (RideablePet)pet;
        if (habbo.getHabboInfo().getRiding() != null) {
            habbo.getHabboInfo().dismountPet();
            return;
        }
        if (rideablePet.getRider() != null) {
            return;
        }
        if (!rideablePet.anyoneCanRide() && habbo.getHabboInfo().getId() != rideablePet.getUserId()) {
            return;
        }
        List<RoomTile> availableTiles = room.getLayout().getWalkableTilesAround(pet.getRoomUnit().getCurrentLocation());
        if (availableTiles.isEmpty()) {
            return;
        }
        RoomTile goalTile = availableTiles.get(0);
        habbo.getRoomUnit().setGoalLocation(goalTile);
        Emulator.getThreading().run(new RoomUnitRidePet(rideablePet, habbo, goalTile));
        rideablePet.getRoomUnit().setWalkTimeOut(3 + Emulator.getIntUnixTimestamp());
        rideablePet.getRoomUnit().stopWalking();
    }
}

