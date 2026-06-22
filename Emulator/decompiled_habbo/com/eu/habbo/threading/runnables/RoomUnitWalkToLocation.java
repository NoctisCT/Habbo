/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.rooms.RoomUnitStatus;
import java.util.ArrayList;
import java.util.List;

public class RoomUnitWalkToLocation
implements Runnable {
    private RoomUnit walker;
    private RoomTile goalTile;
    private Room room;
    private List<Runnable> targetReached;
    private List<Runnable> failedReached;

    public RoomUnitWalkToLocation(RoomUnit walker, RoomTile goalTile, Room room, Runnable targetReached, Runnable failedReached) {
        this.walker = walker;
        this.goalTile = goalTile;
        this.room = room;
        this.targetReached = new ArrayList<Runnable>();
        if (targetReached != null) {
            this.targetReached.add(targetReached);
        }
        this.failedReached = new ArrayList<Runnable>();
        if (failedReached != null) {
            this.targetReached.add(failedReached);
        }
    }

    public RoomUnitWalkToLocation(RoomUnit walker, RoomTile goalTile, Room room, List<Runnable> targetReached, List<Runnable> failedReached) {
        this.walker = walker;
        this.goalTile = goalTile;
        this.room = room;
        this.targetReached = targetReached;
        this.failedReached = failedReached;
    }

    @Override
    public void run() {
        if (this.goalTile == null || this.walker == null || this.room == null || this.walker.getRoom() == null || this.walker.getRoom().getId() != this.room.getId()) {
            this.onFail();
            return;
        }
        if (this.walker.getCurrentLocation().equals(this.goalTile)) {
            this.onSuccess();
            return;
        }
        if (!this.walker.getGoal().equals(this.goalTile) || this.walker.getPath().size() == 0 && !this.walker.hasStatus(RoomUnitStatus.MOVE)) {
            this.onFail();
            return;
        }
        if (!(this.walker.getGoal().equals(this.goalTile) && (this.walker.getPath().size() != 0 || this.walker.hasStatus(RoomUnitStatus.MOVE) && this.walker.hasStatus(RoomUnitStatus.SNOWWAR_RUN)))) {
            this.onFail();
            return;
        }
        Emulator.getThreading().run(this, 250L);
    }

    private void onSuccess() {
        for (Runnable r : this.targetReached) {
            Emulator.getThreading().run(r);
        }
    }

    private void onFail() {
        for (Runnable r : this.failedReached) {
            Emulator.getThreading().run(r);
        }
    }
}

