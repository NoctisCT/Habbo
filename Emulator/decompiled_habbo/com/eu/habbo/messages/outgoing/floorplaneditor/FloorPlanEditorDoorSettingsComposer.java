/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.floorplaneditor;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

public class FloorPlanEditorDoorSettingsComposer
extends MessageComposer {
    private final Room room;

    public FloorPlanEditorDoorSettingsComposer(Room room) {
        this.room = room;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(1664);
        this.response.appendInt(this.room.getLayout().getDoorX());
        this.response.appendInt(this.room.getLayout().getDoorY());
        this.response.appendInt(this.room.getLayout().getDoorDirection());
        return this.response;
    }

    public Room getRoom() {
        return this.room;
    }
}

