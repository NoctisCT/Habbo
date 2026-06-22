/*
 * Decompiled with CFR 0.152.
 */
package com.eu.habbo.habbohotel.modtool;

public enum ModToolTicketState {
    CLOSED(0),
    OPEN(1),
    PICKED(2);

    private final int state;

    private ModToolTicketState(int state) {
        this.state = state;
    }

    public static ModToolTicketState getState(int number) {
        for (ModToolTicketState s : ModToolTicketState.values()) {
            if (s.state != number) continue;
            return s;
        }
        return CLOSED;
    }

    public int getState() {
        return this.state;
    }
}

