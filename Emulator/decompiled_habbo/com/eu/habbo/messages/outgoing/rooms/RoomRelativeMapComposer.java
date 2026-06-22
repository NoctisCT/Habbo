/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class RoomRelativeMapComposer
extends MessageComposer {
    private final Room room;

    public RoomRelativeMapComposer(Room room) {
        this.room = room;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(2753);
        this.response.appendInt(this.room.getLayout().getMapSize() / this.room.getLayout().getMapSizeY());
        this.response.appendInt(this.room.getLayout().getMapSize());
        for (short y = 0; y < this.room.getLayout().getMapSizeY(); y = (short)((short)(y + 1))) {
            for (short x = 0; x < this.room.getLayout().getMapSizeX(); x = (short)(x + 1)) {
                RoomTile t = this.room.getLayout().getTile(x, y);
                if (t != null) {
                    if (Emulator.getConfig().getBoolean("custom.stacking.enabled")) {
                        this.response.appendShort((short)((double)t.z * 256.0));
                        continue;
                    }
                    this.response.appendShort(t.relativeHeight());
                    continue;
                }
                this.response.appendShort(Short.MAX_VALUE);
            }
        }
        return this.response;
    }

    public Room getRoom() {
        return this.room;
    }
}

