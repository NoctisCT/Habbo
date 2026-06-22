/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.incoming.rooms.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.incoming.MessageHandler;

public class UnbanRoomUserEvent
extends MessageHandler {
    @Override
    public void handle() throws Exception {
        int userId = this.packet.readInt();
        int roomId = this.packet.readInt();
        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(roomId);
        if (room != null && room.isOwner(this.client.getHabbo())) {
            room.unbanHabbo(userId);
        }
    }
}

