/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.threading.runnables.hopper;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.interactions.InteractionCostumeHopper;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.rooms.RoomUserRotation;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserStatusComposer;
import com.eu.habbo.threading.runnables.HabboItemNewState;
import com.eu.habbo.threading.runnables.hopper.HopperActionFour;

class HopperActionThree
implements Runnable {
    private final HabboItem teleportOne;
    private final Room room;
    private final GameClient client;
    private final int targetRoomId;
    private final int targetItemId;

    public HopperActionThree(HabboItem teleportOne, Room room, GameClient client, int targetRoomId, int targetItemId) {
        this.teleportOne = teleportOne;
        this.room = room;
        this.client = client;
        this.targetRoomId = targetRoomId;
        this.targetItemId = targetItemId;
    }

    @Override
    public void run() {
        HabboItem targetTeleport;
        Room targetRoom = this.room;
        if (this.teleportOne.getRoomId() != this.targetRoomId) {
            Emulator.getGameEnvironment().getRoomManager().leaveRoom(this.client.getHabbo(), this.room, false);
            targetRoom = Emulator.getGameEnvironment().getRoomManager().loadRoom(this.targetRoomId);
            Emulator.getGameEnvironment().getRoomManager().enterRoom(this.client.getHabbo(), targetRoom.getId(), "", false);
        }
        if ((targetTeleport = targetRoom.getHabboItem(this.targetItemId)) == null) {
            this.client.getHabbo().getRoomUnit().removeStatus(RoomUnitStatus.MOVE);
            this.client.getHabbo().getRoomUnit().setCanWalk(true);
            return;
        }
        targetTeleport.setExtradata("2");
        targetRoom.updateItem(targetTeleport);
        this.client.getHabbo().getRoomUnit().setLocation(this.room.getLayout().getTile(targetTeleport.getX(), targetTeleport.getY()));
        this.client.getHabbo().getRoomUnit().setPreviousLocationZ(targetTeleport.getZ());
        this.client.getHabbo().getRoomUnit().setZ(targetTeleport.getZ());
        this.client.getHabbo().getRoomUnit().setRotation(RoomUserRotation.values()[targetTeleport.getRotation() % 8]);
        this.client.getHabbo().getRoomUnit().removeStatus(RoomUnitStatus.MOVE);
        targetRoom.sendComposer(new RoomUserStatusComposer(this.client.getHabbo().getRoomUnit()).compose());
        Emulator.getThreading().run(new HabboItemNewState(this.teleportOne, this.room, "0"), 500L);
        Emulator.getThreading().run(new HopperActionFour(targetTeleport, targetRoom, this.client), 500L);
        if (targetTeleport instanceof InteractionCostumeHopper) {
            AchievementManager.progressAchievement(this.client.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("CostumeHopper"));
        }
    }
}

