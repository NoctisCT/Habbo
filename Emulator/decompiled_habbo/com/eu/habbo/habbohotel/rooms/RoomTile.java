/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.rooms;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.RoomTileState;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import gnu.trove.set.hash.THashSet;
import java.util.ArrayList;
import java.util.List;

public class RoomTile {
    public final short x;
    public final short y;
    public final short z;
    private final THashSet<RoomUnit> units;
    public RoomTileState state;
    private double stackHeight;
    private boolean allowStack = true;
    private RoomTile previous = null;
    private boolean diagonally;
    private short gCosts;
    private short hCosts;

    public RoomTile(short x, short y, short z, RoomTileState state, boolean allowStack) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.stackHeight = z;
        this.state = state;
        this.setAllowStack(allowStack);
        this.units = new THashSet();
    }

    public RoomTile(RoomTile tile) {
        this.x = tile.x;
        this.y = tile.y;
        this.z = tile.z;
        this.stackHeight = tile.stackHeight;
        this.state = tile.state;
        this.allowStack = tile.allowStack;
        this.diagonally = tile.diagonally;
        this.gCosts = tile.gCosts;
        this.hCosts = tile.hCosts;
        if (this.state == RoomTileState.INVALID) {
            this.allowStack = false;
        }
        this.units = tile.units;
    }

    public RoomTile() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.stackHeight = 0.0;
        this.state = RoomTileState.INVALID;
        this.allowStack = false;
        this.diagonally = false;
        this.gCosts = 0;
        this.hCosts = 0;
        this.units = null;
    }

    public double getStackHeight() {
        return this.stackHeight;
    }

    public void setStackHeight(double stackHeight) {
        if (this.state == RoomTileState.INVALID) {
            this.stackHeight = 32767.0;
            this.allowStack = false;
            return;
        }
        if (stackHeight >= 0.0 && stackHeight != 32767.0) {
            this.stackHeight = stackHeight;
            this.allowStack = true;
        } else {
            this.allowStack = false;
            this.stackHeight = this.z;
        }
    }

    public boolean getAllowStack() {
        if (this.state == RoomTileState.INVALID) {
            return false;
        }
        return this.allowStack;
    }

    public void setAllowStack(boolean allowStack) {
        this.allowStack = allowStack;
    }

    public short relativeHeight() {
        if (this.state == RoomTileState.INVALID) {
            return Short.MAX_VALUE;
        }
        if (!(this.allowStack || this.state != RoomTileState.BLOCKED && this.state != RoomTileState.SIT)) {
            return 16384;
        }
        return this.allowStack ? (short)(this.getStackHeight() * 256.0) : (short)16384;
    }

    public boolean equals(Object o) {
        return o instanceof RoomTile && ((RoomTile)o).x == this.x && ((RoomTile)o).y == this.y;
    }

    public RoomTile copy() {
        return new RoomTile(this);
    }

    public double distance(RoomTile roomTile) {
        double x = this.x - roomTile.x;
        double y = this.y - roomTile.y;
        return Math.sqrt(x * x + y * y);
    }

    public void isDiagonally(boolean isDiagonally) {
        this.diagonally = isDiagonally;
    }

    public RoomTile getPrevious() {
        return this.previous;
    }

    public void setPrevious(RoomTile previous) {
        this.previous = previous;
    }

    public int getfCosts() {
        return this.gCosts + this.hCosts;
    }

    public int getgCosts() {
        return this.gCosts;
    }

    public void setgCosts(RoomTile previousRoomTile) {
        this.setgCosts(previousRoomTile, this.diagonally ? 14 : 10);
    }

    private void setgCosts(short gCosts) {
        this.gCosts = gCosts;
    }

    void setgCosts(RoomTile previousRoomTile, int basicCost) {
        this.setgCosts((short)(previousRoomTile.getgCosts() + basicCost));
    }

    public int calculategCosts(RoomTile previousRoomTile) {
        if (this.diagonally) {
            return previousRoomTile.getgCosts() + 14;
        }
        return previousRoomTile.getgCosts() + 10;
    }

    public void sethCosts(RoomTile parent) {
        this.hCosts = (short)((Math.abs(this.x - parent.x) + Math.abs(this.y - parent.y)) * (parent.diagonally ? 14 : 10));
    }

    public String toString() {
        return "RoomTile (" + this.x + ", " + this.y + ", " + this.z + "): h: " + this.hCosts + " g: " + this.gCosts + " f: " + this.getfCosts();
    }

    public boolean isWalkable() {
        return this.state == RoomTileState.OPEN;
    }

    public RoomTileState getState() {
        return this.state;
    }

    public void setState(RoomTileState state) {
        this.state = state;
    }

    public boolean is(short x, short y) {
        return this.x == x && this.y == y;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<RoomUnit> getUnits() {
        THashSet<RoomUnit> tHashSet = this.units;
        synchronized (tHashSet) {
            return new ArrayList<RoomUnit>(this.units);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addUnit(RoomUnit unit) {
        THashSet<RoomUnit> tHashSet = this.units;
        synchronized (tHashSet) {
            if (!this.units.contains(unit)) {
                this.units.add(unit);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeUnit(RoomUnit unit) {
        THashSet<RoomUnit> tHashSet = this.units;
        synchronized (tHashSet) {
            this.units.remove(unit);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean hasUnits() {
        THashSet<RoomUnit> tHashSet = this.units;
        synchronized (tHashSet) {
            if (!this.units.isEmpty()) {
                this.units.removeIf(unit -> !unit.getCurrentLocation().equals(this));
            }
            return !this.units.isEmpty();
        }
    }

    public boolean unitIsOnFurniOnTile(RoomUnit unit, Item item) {
        return unit.getX() >= this.x && unit.getX() < this.x + item.getLength() && unit.getY() >= this.y && unit.getY() < this.y + item.getWidth();
    }
}

