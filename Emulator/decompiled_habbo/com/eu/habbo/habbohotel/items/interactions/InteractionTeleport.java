/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.threading.runnables.RoomUnitWalkToLocation;
import com.eu.habbo.threading.runnables.teleport.TeleportActionOne;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class InteractionTeleport
extends HabboItem {
    private int targetId;
    private int targetRoomId;
    private int roomUnitID = -1;
    private boolean walkable;

    public InteractionTeleport(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
        this.walkable = baseItem.allowWalk();
        this.setExtradata("0");
    }

    public InteractionTeleport(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
        this.walkable = item.allowWalk();
        this.setExtradata("0");
    }

    @Override
    public void serializeExtradata(ServerMessage serverMessage) {
        serverMessage.appendInt(this.isLimited() ? 256 : 0);
        serverMessage.appendString(this.getExtradata());
        super.serializeExtradata(serverMessage);
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects) {
        return this.getBaseItem().allowWalk() || roomUnit.getId() == this.roomUnitID;
    }

    @Override
    public boolean isWalkable() {
        return this.walkable;
    }

    private void tryTeleport(GameClient client, Room room) {
        Habbo habbo = client.getHabbo();
        if (habbo == null) {
            return;
        }
        RoomUnit unit = habbo.getRoomUnit();
        if (unit == null) {
            return;
        }
        RoomTile currentLocation = room.getLayout().getTile(this.getX(), this.getY());
        if (currentLocation == null) {
            return;
        }
        RoomTile infrontTile = room.getLayout().getTileInFront(currentLocation, this.getRotation());
        if (!this.canUseTeleport(client, room)) {
            return;
        }
        if (this.roomUnitID == unit.getId() && unit.getCurrentLocation().equals(currentLocation)) {
            this.startTeleport(room, habbo);
            this.walkable = true;
            try {
                super.onClick(client, room, new Object[]{"TOGGLE_OVERRIDE"});
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        } else if (unit.getCurrentLocation().equals(currentLocation) || unit.getCurrentLocation().equals(infrontTile)) {
            this.roomUnitID = unit.getId();
            this.setExtradata("1");
            room.updateItemState(this);
            unit.setGoalLocation(infrontTile);
            ArrayList<Runnable> onSuccess = new ArrayList<Runnable>();
            ArrayList<Runnable> onFail = new ArrayList<Runnable>();
            onSuccess.add(() -> {
                room.updateTile(currentLocation);
                this.tryTeleport(client, room);
                unit.removeOverrideTile(currentLocation);
                unit.setCanLeaveRoomByDoor(true);
                this.walkable = this.getBaseItem().allowWalk();
            });
            onFail.add(() -> {
                this.walkable = this.getBaseItem().allowWalk();
                room.updateTile(currentLocation);
                this.setExtradata("0");
                room.updateItemState(this);
                this.roomUnitID = -1;
                unit.removeOverrideTile(currentLocation);
                unit.setCanLeaveRoomByDoor(true);
            });
            this.walkable = true;
            room.updateTile(currentLocation);
            unit.addOverrideTile(currentLocation);
            unit.setGoalLocation(currentLocation);
            unit.setCanLeaveRoomByDoor(false);
            Emulator.getThreading().run(new RoomUnitWalkToLocation(unit, currentLocation, room, onSuccess, onFail));
        } else {
            ArrayList<Runnable> onSuccess = new ArrayList<Runnable>();
            ArrayList<Runnable> onFail = new ArrayList<Runnable>();
            onSuccess.add(() -> this.tryTeleport(client, room));
            unit.setGoalLocation(infrontTile);
            Emulator.getThreading().run(new RoomUnitWalkToLocation(unit, infrontTile, room, onSuccess, onFail));
        }
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception {
        if (room != null && client != null && objects != null && objects.length <= 1) {
            this.tryTeleport(client, room);
        }
    }

    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
    }

    @Override
    public void run() {
        if (!this.getExtradata().equals("0")) {
            this.setExtradata("0");
            Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());
            if (room != null) {
                room.updateItem(this);
            }
        }
        super.run();
    }

    @Override
    public void onPickUp(Room room) {
        this.targetId = 0;
        this.targetRoomId = 0;
        this.roomUnitID = -1;
        this.setExtradata("0");
    }

    public int getTargetId() {
        return this.targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    public int getTargetRoomId() {
        return this.targetRoomId;
    }

    public void setTargetRoomId(int targetRoomId) {
        this.targetRoomId = targetRoomId;
    }

    @Override
    public boolean allowWiredResetState() {
        return false;
    }

    public boolean canUseTeleport(GameClient client, Room room) {
        Habbo habbo = client.getHabbo();
        if (habbo == null) {
            return false;
        }
        RoomUnit unit = habbo.getRoomUnit();
        if (unit == null) {
            return false;
        }
        return habbo.getHabboInfo().getRiding() == null;
    }

    public void startTeleport(Room room, Habbo habbo) {
        this.startTeleport(room, habbo, 500);
    }

    public void startTeleport(Room room, Habbo habbo, int delay) {
        if (habbo.getRoomUnit().isTeleporting) {
            this.walkable = this.getBaseItem().allowWalk();
            return;
        }
        this.roomUnitID = -1;
        habbo.getRoomUnit().isTeleporting = true;
        Emulator.getThreading().run(new TeleportActionOne(this, room, habbo.getClient()), delay);
    }

    @Override
    public boolean isUsable() {
        return true;
    }

    @Override
    public boolean invalidatesToRoomKick() {
        return true;
    }
}

