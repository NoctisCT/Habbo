/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.incoming.rooms.users;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserTagsComposer;

public class RequestRoomUserTagsEvent
extends MessageHandler {
    @Override
    public void handle() throws Exception {
        Habbo habbo;
        int roomUnitId = this.packet.readInt();
        if (this.client.getHabbo().getHabboInfo().getCurrentRoom() != null && (habbo = this.client.getHabbo().getHabboInfo().getCurrentRoom().getHabboByRoomUnitId(roomUnitId)) != null) {
            this.client.sendResponse(new RoomUserTagsComposer(habbo));
        }
    }
}

