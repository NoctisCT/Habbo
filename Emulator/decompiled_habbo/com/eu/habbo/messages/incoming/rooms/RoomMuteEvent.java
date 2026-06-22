/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.incoming.rooms;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.RoomMutedComposer;

public class RoomMuteEvent
extends MessageHandler {
    @Override
    public void handle() throws Exception {
        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();
        if (room != null && room.isOwner(this.client.getHabbo())) {
            room.setMuted(!room.isMuted());
            this.client.sendResponse(new RoomMutedComposer(room));
        }
    }
}

