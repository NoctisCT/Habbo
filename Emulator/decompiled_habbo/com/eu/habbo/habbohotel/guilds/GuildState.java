/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.guilds;

public enum GuildState {
    OPEN(0),
    EXCLUSIVE(1),
    CLOSED(2),
    LARGE(3),
    LARGE_CLOSED(4);

    public final int state;

    private GuildState(int state) {
        this.state = state;
    }

    public static GuildState valueOf(int state) {
        try {
            return GuildState.values()[state];
        }
        catch (Exception e) {
            return OPEN;
        }
    }
}

