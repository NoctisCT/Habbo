/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.incoming.rooms.pets;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.pets.MonsterplantPet;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.threading.runnables.RoomUnitWalkToLocation;
import java.util.ArrayList;

public class ScratchPetEvent
extends MessageHandler {
    @Override
    public void handle() throws Exception {
        int petId = this.packet.readInt();
        Habbo habbo = this.client.getHabbo();
        if (habbo == null) {
            return;
        }
        Room room = habbo.getHabboInfo().getCurrentRoom();
        if (room == null) {
            return;
        }
        Pet pet = room.getPet(petId);
        if (pet == null) {
            return;
        }
        if (habbo.getHabboStats().petRespectPointsToGive > 0 || pet instanceof MonsterplantPet) {
            ArrayList<Runnable> tasks = new ArrayList<Runnable>();
            tasks.add(() -> {
                pet.scratched(habbo);
                Emulator.getThreading().run(pet);
            });
            RoomTile closestTile = habbo.getRoomUnit().getClosestAdjacentTile(pet.getRoomUnit().getX(), pet.getRoomUnit().getY(), true);
            if (closestTile != null) {
                habbo.getRoomUnit().setGoalLocation(closestTile);
                Emulator.getThreading().run(new RoomUnitWalkToLocation(habbo.getRoomUnit(), closestTile, room, tasks, tasks));
            }
        }
    }
}

