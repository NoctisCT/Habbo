/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.rooms;

public enum RoomState {
    OPEN(0),
    LOCKED(1),
    PASSWORD(2),
    INVISIBLE(3);

    private final int state;

    private RoomState(int state) {
        this.state = state;
    }

    public int getState() {
        return this.state;
    }
}

