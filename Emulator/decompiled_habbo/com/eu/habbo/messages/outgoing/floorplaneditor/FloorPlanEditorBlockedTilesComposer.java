/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.floorplaneditor;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import gnu.trove.set.hash.THashSet;

public class FloorPlanEditorBlockedTilesComposer
extends MessageComposer {
    private final Room room;

    public FloorPlanEditorBlockedTilesComposer(Room room) {
        this.room = room;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(3990);
        THashSet<RoomTile> tileList = this.room.getLockedTiles();
        this.response.appendInt(tileList.size());
        for (RoomTile node : tileList) {
            this.response.appendInt(Integer.valueOf(node.x));
            this.response.appendInt(Integer.valueOf(node.y));
        }
        return this.response;
    }

    public Room getRoom() {
        return this.room;
    }
}

