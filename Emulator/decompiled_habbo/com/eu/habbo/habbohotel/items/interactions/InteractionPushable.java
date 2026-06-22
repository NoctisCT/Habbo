/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionDefault;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomLayout;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.rooms.RoomUserRotation;
import com.eu.habbo.threading.runnables.KickBallAction;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class InteractionPushable
extends InteractionDefault {
    private KickBallAction currentThread;

    public InteractionPushable(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
        this.setExtradata("0");
    }

    public InteractionPushable(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
        this.setExtradata("0");
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects) {
        return true;
    }

    @Override
    public boolean isWalkable() {
        return true;
    }

    @Override
    public void onWalkOff(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        super.onWalkOff(roomUnit, room, objects);
        if (this.currentThread != null && !this.currentThread.dead) {
            return;
        }
        int velocity = this.getWalkOffVelocity(roomUnit, room);
        RoomUserRotation direction = this.getWalkOffDirection(roomUnit, room);
        this.onKick(room, roomUnit, velocity, direction);
        if (velocity > 0) {
            if (this.currentThread != null) {
                this.currentThread.dead = true;
            }
            this.currentThread = new KickBallAction(this, room, roomUnit, direction, velocity, false);
            Emulator.getThreading().run(this.currentThread, 0L);
        }
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception {
        super.onClick(client, room, objects);
        if (client == null) {
            return;
        }
        if (RoomLayout.tilesAdjecent(client.getHabbo().getRoomUnit().getCurrentLocation(), room.getLayout().getTile(this.getX(), this.getY()))) {
            int velocity = this.getTackleVelocity(client.getHabbo().getRoomUnit(), room);
            RoomUserRotation direction = this.getWalkOnDirection(client.getHabbo().getRoomUnit(), room);
            this.onTackle(room, client.getHabbo().getRoomUnit(), velocity, direction);
            if (velocity > 0) {
                if (this.currentThread != null) {
                    this.currentThread.dead = true;
                }
                this.currentThread = new KickBallAction(this, room, client.getHabbo().getRoomUnit(), direction, velocity, false);
                Emulator.getThreading().run(this.currentThread, 0L);
            }
        }
    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {
        RoomUserRotation direction;
        int velocity;
        super.onWalkOn(roomUnit, room, objects);
        boolean isDrag = false;
        if (this.getX() == roomUnit.getGoal().x && this.getY() == roomUnit.getGoal().y) {
            velocity = this.getWalkOnVelocity(roomUnit, room);
            direction = this.getWalkOnDirection(roomUnit, room);
            this.onKick(room, roomUnit, velocity, direction);
        } else {
            velocity = this.getDragVelocity(roomUnit, room);
            direction = this.getDragDirection(roomUnit, room);
            this.onDrag(room, roomUnit, velocity, direction);
            isDrag = true;
        }
        if (velocity > 0) {
            if (this.currentThread != null) {
                this.currentThread.dead = true;
            }
            this.currentThread = new KickBallAction(this, room, roomUnit, direction, velocity, isDrag);
            Emulator.getThreading().run(this.currentThread, 0L);
        }
    }

    public abstract int getWalkOnVelocity(RoomUnit var1, Room var2);

    public abstract RoomUserRotation getWalkOnDirection(RoomUnit var1, Room var2);

    public abstract int getWalkOffVelocity(RoomUnit var1, Room var2);

    public abstract RoomUserRotation getWalkOffDirection(RoomUnit var1, Room var2);

    public abstract int getDragVelocity(RoomUnit var1, Room var2);

    public abstract RoomUserRotation getDragDirection(RoomUnit var1, Room var2);

    public abstract int getTackleVelocity(RoomUnit var1, Room var2);

    public abstract RoomUserRotation getTackleDirection(RoomUnit var1, Room var2);

    public abstract int getNextRollDelay(int var1, int var2);

    public abstract RoomUserRotation getBounceDirection(Room var1, RoomUserRotation var2);

    public abstract boolean validMove(Room var1, RoomTile var2, RoomTile var3);

    public abstract void onDrag(Room var1, RoomUnit var2, int var3, RoomUserRotation var4);

    public abstract void onKick(Room var1, RoomUnit var2, int var3, RoomUserRotation var4);

    public abstract void onTackle(Room var1, RoomUnit var2, int var3, RoomUserRotation var4);

    public abstract void onMove(Room var1, RoomTile var2, RoomTile var3, RoomUserRotation var4, RoomUnit var5, int var6, int var7, int var8);

    public abstract void onBounce(Room var1, RoomUserRotation var2, RoomUserRotation var3, RoomUnit var4);

    public abstract void onStop(Room var1, RoomUnit var2, int var3, int var4);

    public abstract boolean canStillMove(Room var1, RoomTile var2, RoomTile var3, RoomUserRotation var4, RoomUnit var5, int var6, int var7, int var8);
}

