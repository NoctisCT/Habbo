/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import gnu.trove.set.hash.THashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UpdateStackHeightComposer
extends MessageComposer {
    private int x;
    private int y;
    private short z;
    private double height;
    private THashSet<RoomTile> updateTiles;
    private Room room;

    public UpdateStackHeightComposer(int x, int y, short z, double height) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.height = height;
    }

    public UpdateStackHeightComposer(Room room, THashSet<RoomTile> updateTiles) {
        this.updateTiles = updateTiles;
        this.room = room;
    }

    @Override
    protected ServerMessage composeInternal() {
        this.response.init(558);
        if (this.updateTiles != null) {
            ArrayList<RoomTile> tilesCopy = new ArrayList<RoomTile>(this.updateTiles);
            tilesCopy.removeIf(Objects::isNull);
            if (tilesCopy.size() > 127) {
                this.response.appendByte(127);
                for (int i = 0; i < 127; ++i) {
                    RoomTile t = (RoomTile)tilesCopy.get(i);
                    this.response.appendByte(Integer.valueOf(t.x));
                    this.response.appendByte(Integer.valueOf(t.y));
                    if (Emulator.getConfig().getBoolean("custom.stacking.enabled")) {
                        this.response.appendShort((short)((double)t.z * 256.0));
                        continue;
                    }
                    this.response.appendShort(t.relativeHeight());
                }
                List remainingTiles = tilesCopy.subList(127, tilesCopy.size());
                if (!remainingTiles.isEmpty()) {
                    this.room.sendComposer(new UpdateStackHeightComposer(this.room, new THashSet<RoomTile>(remainingTiles)).compose());
                }
                return this.response;
            }
            this.response.appendByte(tilesCopy.size());
            for (RoomTile t : tilesCopy) {
                this.response.appendByte(Integer.valueOf(t.x));
                this.response.appendByte(Integer.valueOf(t.y));
                if (Emulator.getConfig().getBoolean("custom.stacking.enabled")) {
                    this.response.appendShort((short)((double)t.z * 256.0));
                    continue;
                }
                this.response.appendShort(t.relativeHeight());
            }
        } else {
            this.response.appendByte(1);
            this.response.appendByte(this.x);
            this.response.appendByte(this.y);
            if (Emulator.getConfig().getBoolean("custom.stacking.enabled")) {
                this.response.appendShort((short)((double)this.z * 256.0));
            } else {
                this.response.appendShort((int)this.height);
            }
        }
        return this.response;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public short getZ() {
        return this.z;
    }

    public double getHeight() {
        return this.height;
    }

    public THashSet<RoomTile> getUpdateTiles() {
        return this.updateTiles;
    }

    public Room getRoom() {
        return this.room;
    }
}

