/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.incoming.rooms.users;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserIgnoredComposer;

public class UnIgnoreRoomUserEvent
extends MessageHandler {
    @Override
    public void handle() throws Exception {
        String username;
        Habbo habbo;
        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();
        if (room != null && (habbo = room.getHabbo(username = this.packet.readString())) != null && habbo.getHabboStats().allowTalk()) {
            this.client.getHabbo().getHabboStats().unignoreUser(habbo.getHabboInfo().getId());
            this.client.sendResponse(new RoomUserIgnoredComposer(habbo, 3));
        }
    }
}

