/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.threading.runnables;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import com.eu.habbo.habbohotel.users.HabboItem;
import java.util.LinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoomUnitTeleport
implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoomUnitTeleport.class);
    private RoomUnit roomUnit;
    private Room room;
    private int x;
    private int y;
    private double z;
    private int newEffect;

    public RoomUnitTeleport(RoomUnit roomUnit, Room room, int x, int y, double z, int newEffect) {
        this.roomUnit = roomUnit;
        this.room = room;
        this.x = x;
        this.y = y;
        this.z = z;
        this.newEffect = newEffect;
    }

    @Override
    public void run() {
        if (this.roomUnit == null || this.roomUnit.getRoom() == null || this.room.getLayout() == null) {
            return;
        }
        if (this.roomUnit.isLeavingTeleporter) {
            this.roomUnit.isWiredTeleporting = false;
            return;
        }
        RoomTile lastLocation = this.roomUnit.getCurrentLocation();
        RoomTile newLocation = this.room.getLayout().getTile((short)this.x, (short)this.y);
        HabboItem topItem = this.room.getTopItemAt(this.roomUnit.getCurrentLocation().x, this.roomUnit.getCurrentLocation().y);
        if (topItem != null) {
            try {
                topItem.onWalkOff(this.roomUnit, this.room, new Object[]{this});
            }
            catch (Exception e) {
                LOGGER.error("Caught exception", e);
            }
        }
        this.roomUnit.setPath(new LinkedList<RoomTile>());
        this.roomUnit.setCurrentLocation(newLocation);
        this.roomUnit.setPreviousLocation(newLocation);
        this.roomUnit.setZ(this.z);
        this.roomUnit.setPreviousLocationZ(this.z);
        this.roomUnit.removeStatus(RoomUnitStatus.MOVE);
        this.roomUnit.setLocation(newLocation);
        this.roomUnit.statusUpdate(true);
        this.roomUnit.isWiredTeleporting = false;
        this.room.updateHabbosAt(newLocation.x, newLocation.y);
        this.room.updateBotsAt(newLocation.x, newLocation.y);
        topItem = this.room.getTopItemAt(this.x, this.y);
        if (topItem != null && this.roomUnit.getCurrentLocation().equals(this.room.getLayout().getTile((short)this.x, (short)this.y))) {
            try {
                topItem.onWalkOn(this.roomUnit, this.room, new Object[]{lastLocation, newLocation, this});
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }
}

