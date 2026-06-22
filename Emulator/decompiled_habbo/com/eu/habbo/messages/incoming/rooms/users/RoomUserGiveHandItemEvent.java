/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.incoming.rooms.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.threading.runnables.HabboGiveHandItemToHabbo;
import com.eu.habbo.threading.runnables.RoomUnitWalkToRoomUnit;
import java.util.ArrayList;

public class RoomUserGiveHandItemEvent
extends MessageHandler {
    @Override
    public void handle() throws Exception {
        Habbo target;
        int userId = this.packet.readInt();
        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();
        if (room != null && (target = room.getHabbo(userId)) != null) {
            ArrayList<Runnable> executable = new ArrayList<Runnable>();
            executable.add(new HabboGiveHandItemToHabbo(this.client.getHabbo(), target));
            Emulator.getThreading().run(new RoomUnitWalkToRoomUnit(this.client.getHabbo().getRoomUnit(), target.getRoomUnit(), this.client.getHabbo().getHabboInfo().getCurrentRoom(), executable, executable));
        }
    }
}

