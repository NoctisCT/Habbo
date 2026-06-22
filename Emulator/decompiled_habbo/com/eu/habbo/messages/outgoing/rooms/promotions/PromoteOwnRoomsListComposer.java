/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms.promotions;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import java.util.ArrayList;
import java.util.List;

public class PromoteOwnRoomsListComposer
extends MessageComposer {
    private final List<Room> rooms = new ArrayList<Room>();

    public PromoteOwnRoomsListComposer(List<Room> rooms) {
        for (Room room : rooms) {
            if (room.isPromoted()) continue;
            this.rooms.add(room);
        }
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2468);
        this.response.appendBoolean(true);
        this.response.appendInt(this.rooms.size());
        for (Room room : this.rooms) {
            this.response.appendInt(room.getId());
            this.response.appendString(room.getName());
            this.response.appendBoolean(true);
        }
        return this.response;
    }

    public List<Room> getRooms() {
        return this.rooms;
    }
}

