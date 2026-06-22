/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.threading.runnables.teleport;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.interactions.InteractionTeleportTile;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.threading.runnables.HabboItemNewState;
import com.eu.habbo.threading.runnables.RoomUnitWalkToLocation;
import java.util.ArrayList;

class TeleportActionFive
implements Runnable {
    private final HabboItem currentTeleport;
    private final Room room;
    private final GameClient client;

    public TeleportActionFive(HabboItem currentTeleport, Room room, GameClient client) {
        this.currentTeleport = currentTeleport;
        this.client = client;
        this.room = room;
    }

    @Override
    public void run() {
        RoomUnit unit = this.client.getHabbo().getRoomUnit();
        unit.isLeavingTeleporter = false;
        unit.isTeleporting = false;
        unit.setCanWalk(true);
        if (this.client.getHabbo().getHabboInfo().getCurrentRoom() != this.room) {
            return;
        }
        if (this.room.getLayout() == null || this.currentTeleport == null) {
            return;
        }
        RoomTile currentLocation = this.room.getLayout().getTile(this.currentTeleport.getX(), this.currentTeleport.getY());
        RoomTile tile = this.room.getLayout().getTileInFront(currentLocation, this.currentTeleport.getRotation());
        if (tile != null) {
            ArrayList<Runnable> onSuccess = new ArrayList<Runnable>();
            onSuccess.add(() -> {
                unit.setCanLeaveRoomByDoor(true);
                Emulator.getThreading().run(() -> {
                    unit.isLeavingTeleporter = false;
                }, 300L);
            });
            unit.setCanLeaveRoomByDoor(false);
            unit.setGoalLocation(tile);
            unit.statusUpdate(true);
            unit.isLeavingTeleporter = true;
            Emulator.getThreading().run(new RoomUnitWalkToLocation(unit, tile, this.room, onSuccess, onSuccess));
        }
        this.currentTeleport.setExtradata("1");
        this.room.updateItem(this.currentTeleport);
        Emulator.getThreading().run(new HabboItemNewState(this.currentTeleport, this.room, "0"), 1000L);
        HabboItem teleportTile = this.room.getTopItemAt(unit.getX(), unit.getY());
        if (teleportTile != null && teleportTile instanceof InteractionTeleportTile && teleportTile != this.currentTeleport) {
            try {
                teleportTile.onWalkOn(unit, this.room, new Object[0]);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

