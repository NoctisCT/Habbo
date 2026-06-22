/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.rooms.RoomUnitType;
import com.eu.habbo.habbohotel.rooms.RoomUserRotation;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BanzaiRandomTeleport
implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(BanzaiRandomTeleport.class);
    private final HabboItem initialTeleporter;
    private final HabboItem targetTeleporter;
    private final RoomUnit roomUnit;
    private final Room room;
    private RoomUserRotation newRotation;

    public BanzaiRandomTeleport(HabboItem initialTeleporter, HabboItem targetTeleporter, RoomUnit roomUnit, Room room) {
        this(initialTeleporter, targetTeleporter, roomUnit, room, BanzaiRandomTeleport.getRandomRotation());
    }

    public BanzaiRandomTeleport(HabboItem initialTeleporter, HabboItem targetTeleporter, RoomUnit roomUnit, Room room, RoomUserRotation newRotation) {
        this.initialTeleporter = initialTeleporter;
        this.targetTeleporter = targetTeleporter;
        this.roomUnit = roomUnit;
        this.room = room;
        this.newRotation = newRotation;
    }

    private static RoomUserRotation getRandomRotation() {
        RoomUserRotation[] rotations = RoomUserRotation.values();
        return rotations[Emulator.getRandom().nextInt(rotations.length)];
    }

    @Override
    public void run() {
        RoomUnit petUnit;
        if (this.roomUnit == null || this.room == null || this.roomUnit.getCurrentLocation() == null) {
            LOGGER.warn("RoomUnit or Room reference is null, teleport aborted.");
            return;
        }
        RoomTile teleporterTile = this.room.getLayout().getTile(this.initialTeleporter.getX(), this.initialTeleporter.getY());
        RoomTile newLocation = this.room.getLayout().getTile(this.targetTeleporter.getX(), this.targetTeleporter.getY());
        boolean isRiding = this.isUserRiding();
        Habbo rider = isRiding ? this.room.getHabbo(this.roomUnit) : null;
        RoomUnit roomUnit = petUnit = isRiding && rider != null ? rider.getHabboInfo().getRiding().getRoomUnit() : null;
        if (petUnit != null) {
            double baseZ;
            petUnit.setCanWalk(true);
            petUnit.setGoalLocation(teleporterTile);
            petUnit.setCurrentLocation(teleporterTile);
            petUnit.setZ(teleporterTile.getStackHeight());
            this.roomUnit.setRotation(this.newRotation);
            petUnit.setRotation(this.newRotation);
            double finalPetZ = baseZ = this.targetTeleporter.getZ();
            double finalRiderZ = baseZ + 1.0;
            Emulator.getThreading().run(() -> {
                this.room.teleportRoomUnitToLocation(petUnit, newLocation.x, newLocation.y, finalPetZ);
                petUnit.setZ(finalPetZ);
                this.room.teleportRoomUnitToLocation(this.roomUnit, newLocation.x, newLocation.y, finalRiderZ);
                this.roomUnit.setZ(finalRiderZ);
                petUnit.setRotation(this.roomUnit.getBodyRotation());
                petUnit.setBodyRotation(this.roomUnit.getBodyRotation());
                petUnit.setHeadRotation(this.roomUnit.getHeadRotation());
                this.enableWalkingAndUpdateTeleporters(this.roomUnit, petUnit, 650);
            }, 1000L);
        } else {
            double baseZ;
            this.roomUnit.setRotation(this.newRotation);
            double finalRiderZ = baseZ = this.targetTeleporter.getZ();
            Emulator.getThreading().run(() -> {
                this.room.teleportRoomUnitToLocation(this.roomUnit, newLocation.x, newLocation.y, finalRiderZ);
                this.roomUnit.setZ(finalRiderZ);
                this.enableWalkingAndUpdateTeleporters(this.roomUnit, null, 650);
            }, 700L);
        }
    }

    private boolean isUserRiding() {
        if (this.roomUnit.getRoomUnitType() != RoomUnitType.USER) {
            return false;
        }
        Habbo habbo = this.room.getHabbo(this.roomUnit);
        return habbo != null && habbo.getHabboInfo().getRiding() != null;
    }

    private void enableWalkingAndUpdateTeleporters(RoomUnit roomUnit, RoomUnit petUnit, int delay) {
        Emulator.getThreading().run(() -> {
            roomUnit.setCanWalk(true);
            if (petUnit != null) {
                petUnit.setCanWalk(true);
            }
            if ("1".equals(this.initialTeleporter.getExtradata())) {
                this.initialTeleporter.setExtradata("0");
                this.room.updateItemState(this.initialTeleporter);
            }
            if ("1".equals(this.targetTeleporter.getExtradata())) {
                this.targetTeleporter.setExtradata("0");
                this.room.updateItemState(this.targetTeleporter);
            }
        }, delay);
    }
}

