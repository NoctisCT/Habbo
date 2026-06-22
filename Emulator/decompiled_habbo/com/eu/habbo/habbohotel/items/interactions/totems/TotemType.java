/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items.interactions.totems;

public enum TotemType {
    NONE(0),
    TROLL(1),
    SNAKE(2),
    BIRD(3);

    public final int type;

    private TotemType(int type) {
        this.type = type;
    }

    public static TotemType fromInt(int type) {
        for (TotemType totemType : TotemType.values()) {
            if (totemType.type != type) continue;
            return totemType;
        }
        return NONE;
    }
}

