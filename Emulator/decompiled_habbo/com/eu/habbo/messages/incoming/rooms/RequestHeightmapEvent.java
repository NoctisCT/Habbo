/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.incoming.rooms;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.incoming.MessageHandler;

public class RequestHeightmapEvent
extends MessageHandler {
    @Override
    public void handle() throws Exception {
        Room room;
        if (this.client.getHabbo().getHabboInfo().getLoadingRoom() > 0 && (room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.client.getHabbo().getHabboInfo().getLoadingRoom())) != null) {
            Emulator.getGameEnvironment().getRoomManager().enterRoom(this.client.getHabbo(), room);
        }
    }
}

