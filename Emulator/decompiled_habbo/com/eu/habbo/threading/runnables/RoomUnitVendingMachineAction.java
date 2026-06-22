/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoomUnitVendingMachineAction
implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoomUnitVendingMachineAction.class);
    private final Habbo habbo;
    private final HabboItem habboItem;
    private final Room room;

    public RoomUnitVendingMachineAction(Habbo habbo, HabboItem habboItem, Room room) {
        this.habbo = habbo;
        this.habboItem = habboItem;
        this.room = room;
    }

    @Override
    public void run() {
        RoomTile tile;
        if (this.habbo.getHabboInfo().getCurrentRoom() == this.room && this.habboItem.getRoomId() == this.room.getId() && (tile = HabboItem.getSquareInFront(this.room.getLayout(), this.habboItem)) != null && this.habbo.getRoomUnit().getGoal().equals(tile)) {
            if (this.habbo.getRoomUnit().getCurrentLocation().equals(tile)) {
                try {
                    this.habboItem.onClick(this.habbo.getClient(), this.room, new Object[]{0});
                }
                catch (Exception e) {
                    LOGGER.error("Caught exception", e);
                }
            } else if (this.room.getLayout().getTile(tile.x, tile.y).isWalkable()) {
                this.habbo.getRoomUnit().setGoalLocation(tile);
                Emulator.getThreading().run(this, this.habbo.getRoomUnit().getPath().size() + 1020);
            }
        }
    }
}

