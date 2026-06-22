/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import gnu.trove.map.hash.THashMap;
import java.util.Map;

public class RoomRightsListComposer
extends MessageComposer {
    private final Room room;

    public RoomRightsListComposer(Room room) {
        this.room = room;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1284);
        this.response.appendInt(this.room.getId());
        THashMap<Integer, String> rightsMap = this.room.getUsersWithRights();
        this.response.appendInt(rightsMap.size());
        for (Map.Entry<Integer, String> set : rightsMap.entrySet()) {
            this.response.appendInt(set.getKey());
            this.response.appendString(set.getValue());
        }
        return this.response;
    }

    public Room getRoom() {
        return this.room;
    }
}

