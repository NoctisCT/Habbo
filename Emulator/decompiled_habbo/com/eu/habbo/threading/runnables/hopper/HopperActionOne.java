/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.threading.runnables.hopper;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.rooms.RoomUserRotation;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserStatusComposer;
import com.eu.habbo.threading.runnables.hopper.HopperActionTwo;

public class HopperActionOne
implements Runnable {
    private final HabboItem teleportOne;
    private final Room room;
    private final GameClient client;

    public HopperActionOne(HabboItem teleportOne, Room room, GameClient client) {
        this.teleportOne = teleportOne;
        this.room = room;
        this.client = client;
    }

    @Override
    public void run() {
        this.client.getHabbo().getRoomUnit().setRotation(RoomUserRotation.values()[(this.teleportOne.getRotation() + 4) % 8]);
        this.client.getHabbo().getRoomUnit().setStatus(RoomUnitStatus.MOVE, this.teleportOne.getX() + "," + this.teleportOne.getY() + "," + this.teleportOne.getZ());
        this.room.scheduledComposers.add(new RoomUserStatusComposer(this.client.getHabbo().getRoomUnit()).compose());
        this.client.getHabbo().getRoomUnit().setLocation(this.room.getLayout().getTile(this.teleportOne.getX(), this.teleportOne.getY()));
        this.client.getHabbo().getRoomUnit().setZ(this.teleportOne.getZ());
        this.client.getHabbo().getRoomUnit().setPreviousLocationZ(this.teleportOne.getZ());
        Emulator.getThreading().run(() -> {
            this.client.getHabbo().getRoomUnit().removeStatus(RoomUnitStatus.MOVE);
            this.room.sendComposer(new RoomUserStatusComposer(this.client.getHabbo().getRoomUnit()).compose());
        }, 750L);
        Emulator.getThreading().run(new HopperActionTwo(this.teleportOne, this.room, this.client), 1250L);
    }
}

