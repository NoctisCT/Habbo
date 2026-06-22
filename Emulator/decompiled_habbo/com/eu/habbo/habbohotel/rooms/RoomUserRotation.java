/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.rooms;

public enum RoomUserRotation {
    NORTH(0),
    NORTH_EAST(1),
    EAST(2),
    SOUTH_EAST(3),
    SOUTH(4),
    SOUTH_WEST(5),
    WEST(6),
    NORTH_WEST(7);

    private final int direction;

    private RoomUserRotation(int direction) {
        this.direction = direction;
    }

    public static RoomUserRotation fromValue(int rotation) {
        rotation %= 8;
        for (RoomUserRotation rot : RoomUserRotation.values()) {
            if (rot.getValue() != rotation) continue;
            return rot;
        }
        return NORTH;
    }

    public static RoomUserRotation counterClockwise(RoomUserRotation rotation) {
        return RoomUserRotation.fromValue(rotation.getValue() + 7);
    }

    public static RoomUserRotation clockwise(RoomUserRotation rotation) {
        return RoomUserRotation.fromValue(rotation.getValue() + 9);
    }

    public int getValue() {
        return this.direction;
    }

    public RoomUserRotation getOpposite() {
        switch (this.ordinal()) {
            case 0: {
                return SOUTH;
            }
            case 1: {
                return SOUTH_WEST;
            }
            case 2: {
                return WEST;
            }
            case 3: {
                return NORTH_WEST;
            }
            case 4: {
                return NORTH;
            }
            case 5: {
                return NORTH_EAST;
            }
            case 6: {
                return EAST;
            }
            case 7: {
                return SOUTH_EAST;
            }
        }
        return null;
    }
}

