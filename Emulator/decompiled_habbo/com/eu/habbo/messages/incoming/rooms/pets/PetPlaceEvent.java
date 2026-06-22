/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.incoming.rooms.pets;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.rooms.RoomUnitType;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.generic.alerts.PetErrorComposer;
import com.eu.habbo.messages.outgoing.inventory.RemovePetComposer;
import com.eu.habbo.messages.outgoing.rooms.pets.RoomPetComposer;

public class PetPlaceEvent
extends MessageHandler {
    @Override
    public void handle() throws Exception {
        RoomTile tile;
        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();
        if (room == null) {
            return;
        }
        if (!(this.client.getHabbo().getHabboInfo().getId() == room.getOwnerId() || room.isAllowPets() || this.client.getHabbo().hasPermission(Permission.ACC_ANYROOMOWNER) || this.client.getHabbo().hasPermission(Permission.ACC_PLACEFURNI))) {
            this.client.sendResponse(new PetErrorComposer(1));
            return;
        }
        int petId = this.packet.readInt();
        Pet pet = this.client.getHabbo().getInventory().getPetsComponent().getPet(petId);
        if (pet == null) {
            return;
        }
        if (room.getCurrentPets().size() >= Room.MAXIMUM_PETS && !this.client.getHabbo().hasPermission(Permission.ACC_UNLIMITED_PETS)) {
            this.client.sendResponse(new PetErrorComposer(2));
            return;
        }
        int x = this.packet.readInt();
        int y = this.packet.readInt();
        RoomTile playerTile = this.client.getHabbo().getRoomUnit().getCurrentLocation();
        if (x == 0 && y == 0 || !room.isOwner(this.client.getHabbo())) {
            tile = room.getLayout().getTileInFront(this.client.getHabbo().getRoomUnit().getCurrentLocation(), this.client.getHabbo().getRoomUnit().getBodyRotation().getValue());
            if (tile == null || !tile.isWalkable()) {
                this.client.sendResponse(new PetErrorComposer(4));
            }
            if (!(tile != null && tile.isWalkable() || (tile = playerTile) != null && tile.isWalkable())) {
                tile = room.getLayout().getDoorTile();
            }
        } else {
            tile = room.getLayout().getTile((short)x, (short)y);
        }
        if (tile == null || !tile.isWalkable() || !tile.getAllowStack()) {
            this.client.sendResponse(new PetErrorComposer(3));
            return;
        }
        pet.setRoom(room);
        RoomUnit roomUnit = pet.getRoomUnit();
        if (roomUnit == null) {
            roomUnit = new RoomUnit();
        }
        roomUnit.setPathFinderRoom(room);
        roomUnit.setLocation(tile);
        roomUnit.setZ(tile.getStackHeight());
        roomUnit.setStatus(RoomUnitStatus.SIT, "0");
        roomUnit.setRoomUnitType(RoomUnitType.PET);
        if (playerTile != null) {
            roomUnit.lookAtPoint(playerTile);
        }
        pet.setRoomUnit(roomUnit);
        room.addPet(pet);
        pet.needsUpdate = true;
        Emulator.getThreading().run(pet);
        room.sendComposer(new RoomPetComposer(pet).compose());
        this.client.getHabbo().getInventory().getPetsComponent().removePet(pet);
        this.client.sendResponse(new RemovePetComposer(pet));
    }
}

