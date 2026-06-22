/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.items.interactions.totems;

public enum TotemPlanetType {
    MOON(0),
    SUN(1),
    EARTH(2);

    public final int type;

    private TotemPlanetType(int type) {
        this.type = type;
    }

    public static TotemPlanetType fromInt(int type) {
        for (TotemPlanetType planetType : TotemPlanetType.values()) {
            if (planetType.type != type) continue;
            return planetType;
        }
        return MOON;
    }
}

